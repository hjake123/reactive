package com.hyperlynx.reactive.util;

import com.hyperlynx.reactive.blocks.WarpSpongeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// This class provides a way to check the contents of the surrounding area with a cache.
// It should be instantiated by classes that use it.
public class AreaMemory {
    BlockPos hostPos;
    Map<Block, BlockPos> model;
    public boolean cache_only_mode = false; // If this is true, the model is used exclusively; no new items are scanned in.

    public AreaMemory(BlockPos hostPos){
        this.hostPos = hostPos;
        model = new HashMap<>();
    }

    public boolean exists(Level l, int radius, Block target){
        return fetch(l, radius, target) != null;
    }

    // Fetch the first instance of the relevant block it can find. If there is none, returns null.
    public BlockPos fetch(Level l, int radius, Block target){
        if(model.containsKey(target)) {
            BlockPos holder = model.get(target);
            if(l.getBlockState(holder).is(target)) {
                return holder;
            }
        }

        if(cache_only_mode)
            return null;

        // If we reach this point, the block must either not be cached or have changed. Either way...
        BlockPos newlyFound = findAndAddNearest(l, radius, target);
        if(newlyFound != null)
            model.put(target, newlyFound);
        return newlyFound;
    }

    // Scan for a compatible block. This is expensive! If there is none, returns null.
    private BlockPos findAndAddNearest(Level l, int radius, Block target){
        Optional<BlockPos> found_maybe = BlockPos.findClosestMatch(hostPos, radius, radius, blockPos -> l.getBlockState(blockPos).is(target));
        return found_maybe.orElse(null);
    }

    // Like exists, but only for blocks right above hostPos.
    public boolean existsAbove(Level l, int range, Block target){
        return fetchAbove(l, range, target) != null;
    }

    // Scan for a compatible block right above. This is a little expensive and doesn't use a cache! If there is none, returns null.
    public BlockPos fetchAbove(Level l, int range, Block target){
        for(int i = 1; i < range; i++){
            if(l.getBlockState(hostPos.above(i)).is(target)){
                return hostPos.above(i);
            }else if(WarpSpongeBlock.stateIsBlocking(l.getBlockState(hostPos.above(i)))){ // Note that this is too specific.
                return null;
            }
        }
        return null;
    }

}
