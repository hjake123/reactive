package com.hyperlynx.reactive.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.function.Predicate;

// This class provides a way to check the contents of the surrounding area with a cache.
// It should be instantiated by classes that use it.
public class AreaMemory {
    BlockPos hostPos;
    Map<Block, BlockPos> model;

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

        // If we reach this point, the block must either not be cached or have changed. Either way...
        BlockPos newlyFound = findAndAddNearest(l, radius, target);
        if(newlyFound != null)
            model.put(target, newlyFound);
        return newlyFound;
    }

    // Scan for a compatible block. This is expensive! If there is none, returns null.
    private BlockPos findAndAddNearest(Level l, int radius, Block target){
        Optional<BlockPos> found_maybe = BlockPos.findClosestMatch(hostPos, radius, radius, blockPos -> l.getBlockState(blockPos).is(target));

        if(!found_maybe.isPresent())
            return null;

        return found_maybe.get();
    }

//    // Positions are ordered based on their distance from the host, closer first.
//    class ProximityComparator implements Comparator<BlockPos>{
//        @Override
//        public int compare(BlockPos o1, BlockPos o2) {
//            if(o1.distManhattan(hostPos) > o2.distManhattan(hostPos))
//                return 1;
//            else if(o1.distManhattan(hostPos) < o2.distManhattan(hostPos))
//                return -1;
//            return 0;
//        }
//    }

}
