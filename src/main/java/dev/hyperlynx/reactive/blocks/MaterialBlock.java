package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.be.MaterialBlockEntity;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A block whose properties are determines by its associated BlockEntity and the Material it is attached to.
/// See [dev.hyperlynx.reactive.alchemy.material]
public class MaterialBlock extends Block implements EntityBlock {
    public MaterialBlock() {
        super(BlockBehaviour.Properties.of());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MaterialBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            @SuppressWarnings("DataFlowIssue") // It's confirmed to exist already so there is no issue.
            int material_id = stack.get(ReactiveComponentTypes.MATERIAL_ID.get());
            if(level.getBlockEntity(pos) instanceof MaterialBlockEntity mbe) {
                mbe.setMaterial(level, material_id);
            }
        }
    }

    private Material material(BlockGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(!(entity instanceof MaterialBlockEntity material_entity)) {
            // ReactiveMod.LOGGER.error("Missing material entity for block at " + pos.toShortString() + ".");
            return Material.empty();
        }
        return material_entity.getMaterial();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        MaterialBlockEntity mbe = (MaterialBlockEntity) level.getBlockEntity(pos);
        stack.set(ReactiveComponentTypes.MATERIAL_ID.get(), mbe.getId());
        return stack;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity entity = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if(entity instanceof MaterialBlockEntity mbe) {
            ItemStack stack = ReactiveItems.MATERIAL.get().getDefaultInstance();
            stack.set(ReactiveComponentTypes.MATERIAL_ID.get(), mbe.getId());
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
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) { // Working!
        float speed = material(level, pos).getOrDefault(MaterialProperties.BREAK_SPEED.get(), 1.0F);
        if (speed == -1.0F) {
            return 0.0F;
        } else {
            int neo_check = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
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
        return SoundType.SLIME_BLOCK; // TODO figure out serialization
    }

    @Override
    public boolean isStickyBlock(BlockState state) {
        return super.isStickyBlock(state);
        // TODO -- can't override normally, so maybe some block state stuff or mixins?
    }

    @Override
    public float getSpeedFactor() {
        return super.getSpeedFactor();
        // TODO -- can't override normally, so maybe mixins?
    }

    @Override
    public float getJumpFactor() {
        return super.getJumpFactor();
        // TODO -- can't override normally, so maybe mixins?
    }

    public static int getBlockColor(BlockState state, @NotNull BlockAndTintGetter getter, BlockPos pos, int index) {
        BlockEntity entity = getter.getBlockEntity(pos);
        if(!(entity instanceof MaterialBlockEntity mbe)) {
            return 0;
        }
        Color color = mbe.getMaterial().getOrDefault(MaterialProperties.COLOR.get(), Color.BLACK);
        return color.hex;
    }
}
