package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public interface ChainDisplacingBlock {
    boolean stateMatchesSelf(BlockState state);

    // Displace connected CBDs if needed.
    default void breadthFirstDisplace(Level level, BlockPos source, boolean displace_surroundings) {
        int max = ConfigMan.COMMON.maxDisplaceCount.get();
        Queue<BlockPos> displace_queue = new ArrayDeque<>();
        displace_queue.add(source);

        int count = 0;
        BlockPos prior = null;
        while(!displace_queue.isEmpty()){
            BlockPos target = displace_queue.poll();
            boolean found_child = false;

            if(count <= max){
                ArrayList<Direction> shuffled_directions = new ArrayList<>(List.of(Direction.values()));
                Collections.shuffle(shuffled_directions);

                for(Direction direction : shuffled_directions){
                    if(displace_queue.contains(target.relative(direction)))
                        continue;
                    if(stateMatchesSelf(level.getBlockState(target.relative(direction)))){
                        displace_queue.add(target.relative(direction));
                        found_child = true;
                        count++;
                    }
                    else if(displace_surroundings
                            && !level.getBlockState(target.relative(direction)).isAir()
                            && !level.getBlockState(target.relative(direction)).is(Registration.VOLT_CELL.get())
                            && !level.getBlockState(target.relative(direction)).is(Registration.DISPLACED_BLOCK.get())){
                        DisplacedBlock.displaceWithChain(level.getBlockState(target.relative(direction)), target.relative(direction), level, 2+count, count, target);
                    }
                }
            }

            if(target.equals(source))
                DisplacedBlock.displace(level.getBlockState(target), target, level, 20);
            else if(source != null)
                DisplacedBlock.displaceWithChain(level.getBlockState(target), target, level, 2 + count, count, prior);
            else
                DisplacedBlock.displace(level.getBlockState(target), target, level, 10 + count);

            if(found_child)
                prior = target;
        }
    }
}
