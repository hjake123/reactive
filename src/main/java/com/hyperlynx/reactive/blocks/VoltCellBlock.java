package com.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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

import java.util.Random;

public class VoltCellBlock extends Block {
    private final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 8, 15);

    public VoltCellBlock(Properties prop) {
        super(prop);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rng) {
        double x = pos.getX() + level.getRandom().nextFloat();
        double z = pos.getZ() + level.getRandom().nextFloat();
        level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, pos.getY() + 0.7 + level.getRandom().nextFloat() * 0.2, z, 0, 0, 0);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity ent) {
        super.stepOn(level, pos, state, ent);
        if(ent instanceof LivingEntity)
            ent.hurt(DamageSource.MAGIC, 1);
    }
}
