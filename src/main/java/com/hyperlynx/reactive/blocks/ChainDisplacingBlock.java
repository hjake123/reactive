package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ChainDisplacingBlock {
    int MAX_CHAIN_DEPTH = 10; // Make configurable!

    boolean stateMatchesSelf(BlockState state);

    // Displace connected Framed blocks.
    default boolean recursiveChainDisplace(Level level, BlockPos pos, BlockPos chain_target, int chain_count, boolean displace_surroundings){
        if(chain_count > MAX_CHAIN_DEPTH)
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


        for(Direction dir : Direction.values()){
            if(recursiveChainDisplace(level, pos.relative(dir), pos, chain_count + 1, displace_surroundings))
                return true;
        }
        return false;
    }
}
