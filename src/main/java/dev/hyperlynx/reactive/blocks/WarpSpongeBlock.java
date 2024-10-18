package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.util.AreaMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

public class WarpSpongeBlock extends WetSpongeBlock {
    public WarpSpongeBlock(Properties props) {
        super(props);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        super.animateTick(state, level, pos, random);
        super.animateTick(state, level, pos, random);
        super.animateTick(state, level, pos, random);
        super.animateTick(state, level, pos, random);
        super.animateTick(state, level, pos, random);
        super.animateTick(state, level, pos, random);
        super.animateTick(state, level, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        final int RANGE = 20;
        for(int i = 1; i < RANGE; i++){
            BlockPos target = pos.below(i);
            if(level.getBlockState(target).is(Blocks.CAULDRON)){
                level.setBlock(target, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), Block.UPDATE_CLIENTS);
            }else if(level.getBlockState(target).is(Blocks.WATER_CAULDRON)){
                level.setBlock(target, level.getBlockState(target).setValue(LayeredCauldronBlock.LEVEL, 3), Block.UPDATE_CLIENTS);
            }else if(level.getBlockState(target).is(Registration.CRUCIBLE.get())){
                level.setBlock(target, level.getBlockState(target).setValue(CrucibleBlock.FULL, true), Block.UPDATE_CLIENTS);
            }else if(AreaMemory.stateIsBlocking(level.getBlockState(target))){
                return;
            }
        }
    }
}
