package dev.hyperlynx.reactive.util;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.alchemy.AlchemyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// Used to determine whether something should be allowed to harvest a block.
public class BlockMoveChecker {

    private static boolean canMoveOrDisplaceBlock(Level level, BlockPos pos, BlockState state) {
        Block candidate_to_break = state.getBlock();
        if(level.getBlockEntity(pos) != null)
            return false;
        if(level.getBlockState(pos).is(AlchemyTags.notRelocatable) || state.isAir())
            return false;
        return !(candidate_to_break.defaultDestroyTime() < 0) && !(candidate_to_break.defaultDestroyTime() > ConfigMan.COMMON.maxMoveBlockBreakTime.get());
    }

    public static boolean canDisplaceBlock(Level level, BlockPos pos, BlockState state) {
        if(level.getBlockState(pos).is(AlchemyTags.doNotDisplace))
            return false;
        return canMoveOrDisplaceBlock(level, pos, state);
    }

    public static boolean canMakeBlockFall(Level level, BlockPos pos, BlockState state) {
        if(state.is(AlchemyTags.doNotBlockFall))
            return false;
        return canMoveOrDisplaceBlock(level, pos, state);
    }



}
