package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.alchemy.AlchemyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public interface ChainDisplacingBlock {
    boolean stateMatchesSelf(BlockState state);

    default void breadthFirstDisplace(Level level, BlockPos source, boolean displace_surroundings){
        breadthFirstDisplace(level, source, displace_surroundings, 20);
    }

    // Displace connected CBDs if needed.
    default void breadthFirstDisplace(Level level, BlockPos source, boolean displace_surroundings, int source_displace_duration) {
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
                    BlockPos next_target = target.relative(direction);
                    if(displace_queue.contains(next_target) || !level.isLoaded(next_target))
                        continue;
                    BlockPos conduct_start = next_target;
                    while(level.getBlockState(next_target).is(AlchemyTags.displaceConductive)
                            && next_target.closerThan(conduct_start, ConfigMan.COMMON.displaceConductRange.get())){
                        next_target = next_target.relative(direction);
                    }
                    if(stateMatchesSelf(level.getBlockState(next_target))){
                        displace_queue.add(next_target);
                        found_child = true;
                        count++;
                    }
                    else if(displace_surroundings
                            && !level.getBlockState(next_target).isAir()
                            && !level.getBlockState(next_target).is(Registration.VOLT_CELL.get())
                            && !level.getBlockState(next_target).is(Registration.DISPLACED_BLOCK.get())){
                        DisplacedBlock.displaceWithChain(level.getBlockState(next_target), next_target, level, 2+count, count, target);
                    }
                }
            }

            if(target.equals(source))
                DisplacedBlock.displace(level.getBlockState(target), target, level, source_displace_duration);
            else if(source != null)
                DisplacedBlock.displaceWithChain(level.getBlockState(target), target, level, 2 + count, count, prior);
            else
                DisplacedBlock.displace(level.getBlockState(target), target, level, 10 + count);

            if(found_child)
                prior = target;
        }
    }
}
