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
    Map<Block, PriorityQueue<BlockPos>> model;

    public AreaMemory(BlockPos hostPos){
        this.hostPos = hostPos;
        model = new HashMap<>();
    }

    public boolean exists(Level l, int radius, Block target){
        return fetch(l, radius, target) != null;
    }

    // Fetch the first instance of the relevant block if can find. If there is none, returns null.
    public BlockPos fetch(Level l, int radius, Block target){
        if(model.containsKey(target) && model.get(target).size() > 0) {
            BlockPos holder = model.get(target).poll();

            // Scan to ensure the block hasn't been removed or changed.
            while(!l.getBlockState(holder).is(target)) {
                holder = model.get(target).poll();
                if(model.get(target).size() == 0){
                    // We failed to find the target block in the whole queue. Find a new location.
                    return findAndAddNearest(l, radius, target);
                }
            }

            // If we reach this point, the holder contains a valid location of the target block.
            // Put it back into the queue.
            model.get(target).add(holder);
            return holder;
        }
        return findAndAddNearest(l, radius, target);
    }

    // Scan for a compatible block. This is expensive! If there is none, returns null.
    private BlockPos findAndAddNearest(Level l, int radius, Block target){
        Optional<BlockPos> found_maybe = BlockPos.findClosestMatch(hostPos, radius, radius, blockPos -> l.getBlockState(blockPos).is(target));

        if(!found_maybe.isPresent())
            return null;

        BlockPos found = found_maybe.get();

        if(!model.containsKey(target))
            model.put(target, new PriorityQueue<>(new ProximityComparator()));
        model.get(target).add(found);
        return found;
    }

    // Positions are ordered based on their distance from the host, closer first.
    class ProximityComparator implements Comparator<BlockPos>{
        @Override
        public int compare(BlockPos o1, BlockPos o2) {
            if(o1.distManhattan(hostPos) > o2.distManhattan(hostPos))
                return 1;
            else if(o1.distManhattan(hostPos) < o2.distManhattan(hostPos))
                return -1;
            return 0;
        }
    }

}
