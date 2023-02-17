package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class WarpSpongeBlock extends WetSpongeBlock {
    public WarpSpongeBlock(Properties props) {
        super(props);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
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
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        final int RANGE = 20;
        for(int i = 1; i < RANGE; i++){
            BlockPos target = pos.below(i);
            if(level.getBlockState(target).is(Blocks.CAULDRON)){
                level.setBlock(target, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), Block.UPDATE_CLIENTS);
            }else if(level.getBlockState(target).is(Blocks.WATER_CAULDRON)){
                level.setBlock(target, level.getBlockState(target).setValue(LayeredCauldronBlock.LEVEL, 3), Block.UPDATE_CLIENTS);
            }else if(level.getBlockState(target).is(Registration.CRUCIBLE.get())){
                level.setBlock(target, level.getBlockState(target).setValue(CrucibleBlock.FULL, true), Block.UPDATE_CLIENTS);
            }else if(stateIsBlocking(level.getBlockState(target))){
                return;
            }
        }
    }

    public static boolean stateIsBlocking(BlockState state){
        return !state.isAir() && (!(state.getBlock() instanceof TrapDoorBlock) || !state.getValue(TrapDoorBlock.OPEN));
    }
}
