package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.ConfigMan;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

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
            if(count > max){
                return;
            }

            BlockPos target = displace_queue.poll();
            if(target.equals(source))
                DisplacedBlock.displace(level.getBlockState(target), target, level, 30);
            else if(source != null)
                DisplacedBlock.displaceWithChain(level.getBlockState(target), target, level, 2+count, prior);
            else
                DisplacedBlock.displace(level.getBlockState(target), target, level, 10+count);

            for(Direction direction :  WorldSpecificValue.shuffle("displacement_spread_direction", List.of(Direction.values()))){
                if(count > max)
                    break;
                if(displace_queue.contains(target.relative(direction)))
                    continue;
                if(stateMatchesSelf(level.getBlockState(target.relative(direction)))
                        || (displace_surroundings
                        && !level.getBlockState(target.relative(direction)).isAir()
                        && !level.getBlockState(target.relative(direction)).is(Registration.VOLT_CELL.get())
                        && !level.getBlockState(target.relative(direction)).is(Registration.DISPLACED_BLOCK.get()))){
                    displace_queue.add(target.relative(direction));
                    count++;
                }
            }
            prior = target;
        }

    }

    // Displace connected CDBs using a recursive depth-first search.
    default boolean recursiveChainDisplace(Level level, BlockPos pos, BlockPos chain_target, int chain_count, boolean displace_surroundings){
        if(chain_count > ConfigMan.COMMON.displaceRange.get())
            return false;

        BlockState state = level.getBlockState(pos);
        if(!this.stateMatchesSelf(state)) {
            if (displace_surroundings && !state.is(Registration.VOLT_CELL.get())) {
                DisplacedBlock.displaceWithChain(level.getBlockState(pos), pos, level, 10 + chain_count * 2, chain_target);
            }
            return false;
        }

        if(pos.equals(chain_target))
            DisplacedBlock.displace(level.getBlockState(pos), pos, level, 60);
        else if(chain_target != null)
            DisplacedBlock.displaceWithChain(state, pos, level, 2+chain_count*2, chain_target);
        else
            DisplacedBlock.displace(level.getBlockState(pos), pos, level, 10+chain_count*2);


        for(Direction dir : WorldSpecificValue.shuffle("displacement_spread_direction", List.of(Direction.values()))){
            if(recursiveChainDisplace(level, pos.relative(dir), pos, chain_count + 1, displace_surroundings))
                return true;
        }
        return false;
    }
}
