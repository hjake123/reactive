package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.be.MaterialBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

/// A block whose properties are determines by its associated BlockEntity and the Material it is attached to.
/// See [dev.hyperlynx.reactive.alchemy.material]
public class MaterialBlock extends Block {
    public MaterialBlock() {
        super(BlockBehaviour.Properties.of());
    }

    private Material material(BlockGetter level, BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(!(entity instanceof MaterialBlockEntity material_entity)) {
            throw new RuntimeException("Missing material entity for block at " + pos.toShortString() + "!");
        }
        return material_entity.getMaterial();
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity victim) {
        if(material(level, pos).has(MaterialProperties.MAGMA_STEP.get())) {
            if (!victim.isSteppingCarefully() && victim instanceof LivingEntity) {
                victim.hurt(level.damageSources().hotFloor(), 1.0F);
            }
        }
    }

    @Override
    public boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
        return super.isFireSource(state, level, pos, direction) || material(level, pos).has(MaterialProperties.FIRE_SOURCE.get());
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float speed = material(level, pos).getOrDefault(MaterialProperties.BREAK_SPEED.get(), 1.0F);
        if (speed == -1.0F) {
            return 0.0F;
        } else {
            int neo_check = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
            return player.getDigSpeed(state, pos) / speed / (float)neo_check;
        }
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return material(level, pos).getOrDefault(MaterialProperties.BLAST_RESISTANCE.get(), 1.0F);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return material(level, pos).getOrDefault(MaterialProperties.ENCHANT_POWER.get(), 0.0F);
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return material(level, pos).getOrDefault(MaterialProperties.FRICTION.get(), 1.0F);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return material(level, pos).getOrDefault(MaterialProperties.FLAMMABILITY.get(), 0);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return material(level, pos).getOrDefault(MaterialProperties.LIGHT.get(), 0);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return material(level, pos).getOrDefault(MaterialProperties.REDSTONE.get(), 0);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return material(level, pos).getOrDefault(MaterialProperties.SOUND_TYPE.get(), SoundType.SLIME_BLOCK);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true; // TODO: sometimes false?
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return super.getPistonPushReaction(state);
        // TODO -- can't override normally, so maybe some block state stuff or mixins?
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
}
