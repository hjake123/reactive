package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialModel;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.be.MaterialBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.items.MaterialItem;
import dev.hyperlynx.reactive.net.MaterialBESyncPayload;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import dev.hyperlynx.reactive.registration.ReactiveSoundEvents;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

/// A block whose properties are determined by its associated BlockEntity and the Material it is attached to.
/// See [Material] and [MaterialItem]
public class MaterialBlock extends Block implements EntityBlock {
    public static final EnumProperty<MaterialModel> MODEL = EnumProperty.create("model", MaterialModel.class);
    public static final BooleanProperty RANDOM_TICKING = BooleanProperty.create("random_ticking");

    public MaterialBlock() {
        super(BlockBehaviour.Properties.of());
        registerDefaultState(this.defaultBlockState().setValue(MODEL, MaterialModel.SALT).setValue(RANDOM_TICKING, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODEL);
        builder.add(RANDOM_TICKING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MaterialBlockEntity(pos, state);
    }

    private BlockState setModelByMaterialId(Level level, BlockState state, ResourceLocation material_id) {
        String model_name = MaterialMan.fetch(level, material_id).getOrDefault(MaterialProperties.MODEL_NAME.get(), "salt");
        return state.setValue(MODEL, MaterialModel.fromName(model_name));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(stack.has(ReactiveComponentTypes.MATERIAL_ID)) {
            @SuppressWarnings("DataFlowIssue") // It's confirmed to exist already so there is no issue.
            ResourceLocation material_id = stack.get(ReactiveComponentTypes.MATERIAL_ID);
            if(level.getBlockEntity(pos) instanceof MaterialBlockEntity mbe) {
                mbe.setMaterial(level, material_id);
            }
            state = setModelByMaterialId(level, state, material_id);
            state = setRandomTicking(level, state, material_id);
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }
    }
    private Material material(BlockGetter getter, BlockPos pos) {
        BlockEntity entity = getter.getBlockEntity(pos);
        if(!(entity instanceof MaterialBlockEntity material_entity)) {
            return Material.empty();
        }
        return material_entity.getMaterial();
    }

    private boolean isGelBlock(BlockGetter getter, BlockPos pos) {
        Material material = material(getter, pos);
        if(!material.has(MaterialProperties.MODEL_NAME.get())) {
            return false;
        }
        return material.get(MaterialProperties.MODEL_NAME.get()).equals(MaterialModel.GEL.getSerializedName());
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if(isGelBlock(getter, pos)) {
            return Shapes.empty();
        }
        return Shapes.block();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if(isGelBlock(level, pos)) {
            if (!(entity instanceof LivingEntity living)) {
                return;
            }
            living.makeStuckInBlock(state, new Vec3(0.9F, 0.9D, 0.9F));
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        MaterialBlockEntity mbe = (MaterialBlockEntity) level.getBlockEntity(pos);
        if(mbe == null || mbe.hasNoValidMaterial()) {
            return ReactiveItems.SALT_BLOCK.get().getDefaultInstance();
        }
        stack.set(ReactiveComponentTypes.MATERIAL_ID, mbe.getMaterialId());
        return stack;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity entity = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if(entity instanceof MaterialBlockEntity mbe) {
            if(mbe.hasNoValidMaterial()) {
                return List.of(ReactiveItems.SALT_BLOCK.get().getDefaultInstance());
            }
            ItemStack stack = ReactiveItems.MATERIAL.get().getDefaultInstance();
            stack.set(ReactiveComponentTypes.MATERIAL_ID.get(), mbe.getMaterialId());
            return List.of(stack);
        }
        return super.getDrops(state, params);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity victim) { // Working!
        if(material(level, pos).has(MaterialProperties.MAGMA_STEP.get())) {
            if (!victim.isSteppingCarefully() && victim instanceof LivingEntity) {
                victim.hurt(level.damageSources().hotFloor(), 1.0F);
            }
        }
    }

    @Override
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) { // Working!
        return super.isFireSource(state, level, pos, direction) || material(level, pos).has(MaterialProperties.FIRE_SOURCE.get());
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        Material material = material(getter, pos);
        Level level = player.level();
        if(!level.isClientSide && material(getter, pos).has(MaterialProperties.SELF_DEFENSE.get()) && getter.getBlockEntity(pos) instanceof MaterialBlockEntity mbe) {
            if(mbe.generic_delay == 0) {
                Vec3 center = pos.getCenter();
                ParticleScribe.drawParticle(level, ParticleTypes.ANGRY_VILLAGER, center.x + level.random.nextFloat() - 0.5, center.y , center.z + level.random.nextFloat() - 0.5);
                float damage = material.get(MaterialProperties.SELF_DEFENSE.get());
                player.hurt(level.damageSources().magic(), damage);
                ParticleScribe.drawParticleZigZag(level, ParticleTypes.ELECTRIC_SPARK,
                        center.x, center.y,center.z,
                        player.getX(), player.getEyeHeight() / 2 + player.getY(), player.getZ(), 8, 10, 0.3);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ReactiveSoundEvents.ZAP.get(), SoundSource.BLOCKS, 0.5F, 0.98F + player.level().random.nextFloat()*0.05F);
                mbe.generic_delay = 40;
            } else {
                mbe.generic_delay--;
            }
        }

        float speed = material.getOrDefault(MaterialProperties.BREAK_STRENGTH.get(), 1.0F);
        if (speed == -1.0F) {
            return 0.0F;
        } else {
            speed *= 0.75F;
            int neo_check = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, getter, pos) ? 30 : 100;
            return player.getDigSpeed(state, pos) / speed / (float)neo_check;
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
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) { // Working... finally!
        if(level.getBlockEntity(pos) instanceof MaterialBlockEntity) {
            return material(level, pos).getOrDefault(MaterialProperties.LIGHT.get(), 0);
        } else {
            return MaterialBlockEntity.lights.getLightAt(pos);
        }
    }

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) { // Works!
        return material(level, pos).getOrDefault(MaterialProperties.REDSTONE.get(), 0);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return MaterialModel.fromName(material(level, pos).getOrDefault(MaterialProperties.MODEL_NAME.get(), "salt")).getSoundType();
    }

    @Override
    public boolean isStickyBlock(BlockState state) {
        return super.isStickyBlock(state);
        // TODO -- can't override normally, so maybe some block state stuff or mixins?
    }

    public static int getBlockColor(BlockState state, @NotNull BlockAndTintGetter getter, BlockPos pos, int index) {
        BlockEntity entity = getter.getBlockEntity(pos);
        if(!(entity instanceof MaterialBlockEntity mbe)) {
            return 0;
        }
        Color color = mbe.getMaterial().getOrDefault(MaterialProperties.COLOR.get(), Color.white());
        return color.hex;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(RANDOM_TICKING);
    }

    private BlockState setRandomTicking(Level level, BlockState state, ResourceLocation material_id) {
        if(MaterialMan.fetch(level, material_id).has(MaterialProperties.WARPING.get())) {
            return state.setValue(RANDOM_TICKING, true);
        }
        return state;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
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

}
