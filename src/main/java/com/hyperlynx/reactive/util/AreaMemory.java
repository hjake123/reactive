package com.hyperlynx.reactive.util;

import com.hyperlynx.reactive.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// This class provides a way to check the contents of the surrounding area with a cache.
// It should be instantiated by classes that use it.
public class AreaMemory {
    BlockPos hostPos;
    Map<Block, BlockPos> model;
    Map<TagKey<Block>, BlockPos> tag_model;
    BlockPos block_above_model;

    public AreaMemory(BlockPos hostPos){
        this.hostPos = hostPos;
        model = new HashMap<>();
        tag_model = new HashMap<>();
    }

    public boolean exists(Level l, Block target){
        return fetch(l, target) != null;
    }

    // Fetch the first instance of the relevant block it can find. If there is none, returns null.
    public BlockPos fetch(Level l, Block target){
        if(model.containsKey(target)) {
            BlockPos holder = model.get(target);
            if(l.getBlockState(holder).is(target)) {
                return holder;
            }
        }

        // If we reach this point, the block must either not be cached or have changed. Either way...
        BlockPos newlyFound = findAndAddNearest(l, ConfigMan.COMMON.areaMemoryRange.get(), target);
        if(newlyFound != null) {
            model.put(target, newlyFound);
        }
        return newlyFound;
    }

    // 7a: Check for blocks of a certain tag in the tag model.
    public BlockPos fetch(Level l, int radius, TagKey<Block> target){
        if(tag_model.containsKey(target)) {
            BlockPos holder = tag_model.get(target);
            if(l.getBlockState(holder).is(target)) {
                return holder;
            }
        }

        // If we reach this point, the block must either not be cached or have changed. Either way...
        BlockPos newlyFound = findAndAddNearest(l, radius, target);
        if(newlyFound != null) {
            tag_model.put(target, newlyFound);
        }
        return newlyFound;
    }
    // Scan for a certain block. This is expensive! If there is none, returns null.

    private BlockPos findAndAddNearest(Level l, int radius, Block target){
        Optional<BlockPos> found_maybe = BlockPos.findClosestMatch(hostPos, radius, radius, blockPos -> l.getBlockState(blockPos).is(target));
        return found_maybe.orElse(null);
    }
    // Scan for a block in the tag. This is expensive! If there is none, returns null.

    private BlockPos findAndAddNearest(Level l, int radius, TagKey<Block> target){
        Optional<BlockPos> found_maybe = BlockPos.findClosestMatch(hostPos, radius, radius, blockPos -> l.getBlockState(blockPos).is(target));
        return found_maybe.orElse(null);
    }
    // Like exists, but only for blocks right above hostPos.
    public boolean existsAbove(Level l, int range, Block target){
        if(block_above_model != null && l.getBlockState(block_above_model).is(target)){
            return true;
        }
        return fetchAbove(l, range, target) != null;
    }

    // Scan for a compatible block right above. This is a little expensive! If there is none, returns null.
    public BlockPos fetchAbove(Level l, int range, Block target){
        for(int i = 1; i < range; i++){
            if(l.getBlockState(hostPos.above(i)).is(target)){
                return hostPos.above(i);
            }else if(stateIsBlocking(l.getBlockState(hostPos.above(i)))){
                return null;
            }
        }
        return null;
    }

    public static boolean stateIsBlocking(BlockState state){
        return !state.isAir() && (!(state.getBlock() instanceof TrapDoorBlock) || !state.getValue(TrapDoorBlock.OPEN));
    }
}
