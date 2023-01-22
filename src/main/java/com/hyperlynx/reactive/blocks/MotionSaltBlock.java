package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.util.HarvestChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MotionSaltBlock extends Block {
    public MotionSaltBlock(Properties props) {
        super(props);
    }

    @Override
    public void onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        causeNeighborToFall(level, pos.above());
        causeNeighborToFall(level, pos.below());
        causeNeighborToFall(level, pos.offset(1, 0, 0));
        causeNeighborToFall(level, pos.offset(-1, 0, 0));
        causeNeighborToFall(level, pos.offset(0, 0, 1));
        causeNeighborToFall(level, pos.offset(0, 0, -1));
    }

    @Override
    public void neighborChanged(BlockState our_state, Level level, BlockPos salt_pos, Block block, BlockPos neighbor_pos, boolean unknown) {
        causeNeighborToFall(level, neighbor_pos);
    }

    private static void causeNeighborToFall(Level level, BlockPos neighbor_pos) {
        if(!level.getBlockState(neighbor_pos.below()).isAir())
            return;

        if(HarvestChecker.canMineBlock(level, neighbor_pos, level.getBlockState(neighbor_pos), 35F)){
            FallingBlockEntity.fall(level, neighbor_pos, level.getBlockState(neighbor_pos));
        }
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 250;
    }
}
