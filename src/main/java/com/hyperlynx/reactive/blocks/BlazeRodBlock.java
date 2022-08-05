package com.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlazeRodBlock extends RotatedPillarBlock {

    protected static final VoxelShape X_SHAPE = Block.box(0, 6, 6, 16, 10, 10);
    protected static final VoxelShape Y_SHAPE = Block.box(6, 0, 6, 10, 16, 10);
    protected static final VoxelShape Z_SHAPE = Block.box(6, 6, 0, 10, 10, 16);

    public BlazeRodBlock(Properties p_154339_) {
        super(p_154339_);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if(state.getValue(AXIS).isVertical()){
            return Y_SHAPE;
        }
        if(state.getValue(AXIS).equals(Direction.Axis.X)){
            return X_SHAPE;
        }
        return Z_SHAPE;
    }
}
