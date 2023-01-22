package com.hyperlynx.reactive.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;

// Used to determine whether something should be allowed to harvest a block.
public class HarvestChecker {
    public static boolean canMineBlock(Level level, Player player, BlockPos pos, BlockState state, float max_destroy_time) {
        if (!player.mayBuild() || !level.mayInteract(player, pos))
            return false;

        if (MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, state, player)))
            return false;

        return canMineBlock(level, pos, state, max_destroy_time);
    }

    public static boolean canMineBlock(Level level, BlockPos pos, BlockState state, float max_destroy_time) {
        Block candidate_to_break = state.getBlock();
        if(level.getBlockEntity(pos) != null)
            return false;
        return !(candidate_to_break.defaultDestroyTime() < 0) && !(candidate_to_break.defaultDestroyTime() > max_destroy_time);
    }
}
