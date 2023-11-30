package com.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class VoltCellBlock extends CellBlock {

    public VoltCellBlock(Properties prop) {
        super(prop);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        double x = pos.getX() + level.getRandom().nextFloat();
        double z = pos.getZ() + level.getRandom().nextFloat();
        level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, pos.getY() + 0.7 + level.getRandom().nextFloat() * 0.2, z, 0, 0, 0);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity ent) {
        super.stepOn(level, pos, state, ent);
        if (ent instanceof LivingEntity)
            ent.hurt(level.damageSources().magic(), 1);
    }
}

