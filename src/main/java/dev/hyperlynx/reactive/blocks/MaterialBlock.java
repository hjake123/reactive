package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialModel;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.be.MaterialBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.items.MaterialItem;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

import static dev.hyperlynx.reactive.items.MaterialItem.MATERIAL_ID_KEY;

/// A block whose properties are determined by its associated BlockEntity and the Material it is attached to.
/// See [Material] and [MaterialItem]
public class MaterialBlock extends Block implements EntityBlock {
    public static final EnumProperty<MaterialModel> MODEL = EnumProperty.create("model", MaterialModel.class);
    public static final BooleanProperty RANDOM_TICKING = BooleanProperty.create("random_ticking");
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light", 0, 15); // You leave me no choice, AuxiliaryLightManager...

    public MaterialBlock() {
        super(Properties.of().noOcclusion().lightLevel(state -> state.getValue(LIGHT_LEVEL)));
        registerDefaultState(this.defaultBlockState().setValue(MODEL, MaterialModel.SALT).setValue(RANDOM_TICKING, false).setValue(LIGHT_LEVEL, 0));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODEL);
        builder.add(RANDOM_TICKING);
        builder.add(LIGHT_LEVEL);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MaterialBlockEntity(pos, state);
    }

    private BlockState setStateByMaterialId(Level level, BlockState state, ResourceLocation material_id) {
        Material material = MaterialMan.fetch(level, material_id);
        String model_name = material.getOrDefault(MaterialProperties.MODEL_NAME.get(), "salt");
        int light_level = material.getOrDefault(MaterialProperties.LIGHT.get(), 0);
        return state.setValue(MODEL, MaterialModel.fromName(model_name)).setValue(LIGHT_LEVEL, light_level);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockState to_place_state = state;
        if(MaterialItem.hasMaterialId(stack)) {
            ResourceLocation material_id = MaterialItem.getMaterialId(stack);
            if(level.getBlockEntity(pos) instanceof MaterialBlockEntity mbe) {
                mbe.setMaterial(level, material_id);
                if(level instanceof ServerLevel slevel) {
                    PacketDistributor.sendToPlayersTrackingChunk(slevel, new ChunkPos(pos), new MaterialBESyncPayload(mbe.getMaterialId(), pos));
                }
            }
            to_place_state = setStateByMaterialId(level, to_place_state, material_id);
            to_place_state = setRandomTicking(level, to_place_state, material_id);
            level.setBlock(pos, to_place_state, Block.UPDATE_CLIENTS);
        }
    }
    private Material material(BlockGetter getter, BlockPos pos) {
        BlockEntity entity = getter.getBlockEntity(pos);
        if(!(entity instanceof MaterialBlockEntity material_entity)) {
            return Material.empty();
        }
        return material_entity.getMaterial();
    }

    private boolean isGelBlock(BlockState state) {
        return state.getValue(MODEL).equals(MaterialModel.GEL);
    }

    private boolean isIntangible(BlockGetter getter, BlockPos pos, @Nullable Entity collider) {
        if(collider != null && material(getter, pos).has(MaterialProperties.SEMITANGIBLE.get())) {
            return collider.isShiftKeyDown();
        }
        return material(getter, pos).has(MaterialProperties.INTANGIBLE.get());
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Entity collider = null;
        if(context instanceof EntityCollisionContext entity_context) {
            collider = entity_context.getEntity();
        }
        if(isGelBlock(state) || isIntangible(getter, pos, collider)) {
            return Shapes.empty();
        }
        return Shapes.block();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }
        if(isIntangible(level, pos, entity)) {
            return;
        }
        living.makeStuckInBlock(state, isGelBlock(state) ? new Vec3(0.9F, 0.9D, 0.9F) : new Vec3(0.4F, 0.4D, 0.4F));
        if(material(level, pos).has(MaterialProperties.MAGMA_STEP.get())) {
            hurtWithMagmaStep(level, entity);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        MaterialBlockEntity mbe = (MaterialBlockEntity) level.getBlockEntity(pos);
        if(mbe == null || mbe.hasNoValidMaterial()) {
            return Registration.SALT_BLOCK_ITEM.get().getDefaultInstance();
        }
        MaterialItem.setMaterialId(stack, mbe.getMaterialId());
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity entity = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if(entity instanceof MaterialBlockEntity mbe) {
            if(mbe.hasNoValidMaterial()) {
                return List.of(Registration.SALT_BLOCK_ITEM.get().getDefaultInstance());
            }
            ItemStack stack = Registration.MATERIAL_ITEM.get().getDefaultInstance();
            MaterialItem.setMaterialId(stack, mbe.getMaterialId());
            return List.of(stack);
        }
        return super.getDrops(state, params);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity victim) { // Working!
        if(material(level, pos).has(MaterialProperties.MAGMA_STEP.get())) {
            hurtWithMagmaStep(level, victim);
        }
    }

    private static void hurtWithMagmaStep(Level level, Entity victim) {
        if (!victim.isSteppingCarefully() && victim instanceof LivingEntity) {
            victim.hurt(level.damageSources().hotFloor(), 1.0F);
        }
    }

    @Override
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) { // Working!
        return super.isFireSource(state, level, pos, direction) || material(level, pos).has(MaterialProperties.FIRE_SOURCE.get());
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        Material material = material(getter, pos);
        Level level = player.level();
        if(!level.isClientSide && material(getter, pos).has(MaterialProperties.SELF_DEFENSE.get()) && getter.getBlockEntity(pos) instanceof MaterialBlockEntity mbe) {
            if(mbe.generic_delay == 0) {
                Vec3 center = pos.getCenter();
                ParticleScribe.drawParticle(level, ParticleTypes.ANGRY_VILLAGER, center.x + level.random.nextFloat() - 0.5, center.y , center.z + level.random.nextFloat() - 0.5);
                float damage = material.get(MaterialProperties.SELF_DEFENSE.get());
                player.hurt(level.damageSources().magic(), damage);
                ParticleScribe.drawParticleZigZag(level, Registration.ACID_BUBBLE_PARTICLE,
                        center.x, center.y,center.z,
                        player.getX(), player.getEyeHeight() / 2 + player.getY(), player.getZ(), 5, 10, 0.3);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, 0.5F, 0.98F + player.level().random.nextFloat()*0.05F);
                mbe.generic_delay = 20;
            } else {
                mbe.generic_delay--;
            }
        }

        float speed = material.getOrDefault(MaterialProperties.BREAK_STRENGTH.get(), 1.0F);
        if (speed == -1.0F) {
            return 0.0F;
        } else {
            speed *= 0.75F;
            int forge_check = net.minecraftforge.event.ForgeEventFactory.doPlayerHarvestCheck(player, state, true) ? 30 : 100;
            return player.getDigSpeed(state, pos) / speed / (float)forge_check;
        }
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) { // Working!
        return material(level, pos).getOrDefault(MaterialProperties.BLAST_RESISTANCE.get(), 1.0F);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) { // Working!
        return material(level, pos).getOrDefault(MaterialProperties.ENCHANT_POWER.get(), 0.0F);
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) { // Seems to work?
        return super.getFriction(state, level, pos, entity) * material(level, pos).getOrDefault(MaterialProperties.FRICTION.get(), 1.0F);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { // Working!
        return material(level, pos).getOrDefault(MaterialProperties.FLAMMABILITY.get(), 0);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { // Works!
        return material(level, pos).getOrDefault(MaterialProperties.REDSTONE.get(), 0);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return MaterialModel.fromName(material(level, pos).getOrDefault(MaterialProperties.MODEL_NAME.get(), "salt")).getSoundType();
    }

    public static int getBlockColor(@NotNull BlockAndTintGetter getter, BlockPos pos) {
        BlockEntity entity = getter.getBlockEntity(pos);
        if(!(entity instanceof MaterialBlockEntity mbe)) {
            return 0;
        }
        Color color = mbe.getMaterial().getOrDefault(MaterialProperties.COLOR.get(), Color.white());
        return color.hex;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(RANDOM_TICKING);
    }

    private BlockState setRandomTicking(Level level, BlockState state, ResourceLocation material_id) {
        if(MaterialMan.fetch(level, material_id).has(MaterialProperties.WARPING.get())) {
            return state.setValue(RANDOM_TICKING, true);
        }
        return state;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(!material(level, pos).has(MaterialProperties.WARPING.get())) {
            level.setBlock(pos, state.setValue(RANDOM_TICKING, false), Block.UPDATE_CLIENTS);
            return;
        }
        // Teleport WARPING materials on random tick
        for(Direction direction : Direction.allShuffled(level.random)) {
            BlockPos adjacent_pos = pos.offset(new Vec3i(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
            BlockState adjacent_state = level.getBlockState(adjacent_pos);
            if(adjacent_state.isAir() || adjacent_state.is(Blocks.FIRE) || adjacent_state.canBeReplaced()) {
                level.setBlock(adjacent_pos, state, Block.UPDATE_CLIENTS);
                if(level.getBlockEntity(pos) instanceof MaterialBlockEntity old_mbe && level.getBlockEntity(adjacent_pos) instanceof MaterialBlockEntity new_mbe) {
                    new_mbe.setMaterial(level, old_mbe.getMaterialId());
                    PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(adjacent_pos), new MaterialBESyncPayload(old_mbe.getMaterialId(), adjacent_pos));
                }
                level.removeBlock(pos, false);
                level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS);
                float f = level.random.nextFloat() * 0.6F + 0.4F;
                ParticleScribe.drawParticleBox(level, new DustParticleOptions(new Vector3f(f * 0.9F, f * 0.3F, f), 0.5F), new AABB(pos),8);
                return;
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if(material(level, pos).has(MaterialProperties.REDSTONE_MELTING.get())) {
            if(level.getDirectSignalTo(pos) > 0) {
                level.setBlock(pos, state.setValue(MODEL, MaterialModel.GEL), Block.UPDATE_CLIENTS);
            } else {
                level.setBlock(pos, state.setValue(MODEL, MaterialModel.valueOf(material(level, pos).get(MaterialProperties.MODEL_NAME.get()).toUpperCase())), Block.UPDATE_CLIENTS);
            }
        }
    }
}
