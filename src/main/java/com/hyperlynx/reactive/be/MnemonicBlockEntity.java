package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.MnemonicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MnemonicBlockEntity extends BlockEntity {
    private static final int MEMORY_SIZE = 256;
    int[] memory = new int[MEMORY_SIZE];
    int index = 0;
    Status status = Status.EMPTY;


    public MnemonicBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.MNEMONIC_BULB_BE_TYPE.value(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MnemonicBlockEntity bulb) {
        int input = level.getDirectSignalTo(pos);
        switch(bulb.status){
            case EMPTY -> {
                if(input == 0){
                    return;
                }
                bulb.index = 0;
                bulb.status = Status.RECORDING;
            }
            case RECORDING -> {
                bulb.memory[bulb.index++] = input;
                if(bulb.index >= MEMORY_SIZE){
                    bulb.status = Status.DISABLED;
                }
                if(state.getValue(MnemonicBlock.CHARGED)){
                    bulb.index = 0;
                    bulb.status = Status.REPLAYING;
                }
            }
            case DISABLED -> {
                if(state.getValue(MnemonicBlock.CHARGED)){
                    bulb.index = 0;
                    bulb.status = Status.REPLAYING;
                } else if (input > 0) {
                    bulb.status = Status.EMPTY;
                }
            }
            case REPLAYING -> {
                if(!state.getValue(MnemonicBlock.CHARGED)){
                    level.setBlock(pos, state.setValue(MnemonicBlock.POWER, 0), Block.UPDATE_ALL);
                    bulb.status = Status.DISABLED;
                }
                int output = bulb.memory[bulb.index++];
                level.setBlock(pos, state.setValue(MnemonicBlock.POWER, output), Block.UPDATE_ALL);
                bulb.index %= MEMORY_SIZE;
            }
        }

    }

    // Need to persist status and memory to disk!

    enum Status {
        EMPTY,
        RECORDING,
        REPLAYING,
        DISABLED
    }
}
