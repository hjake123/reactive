package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.MnemonicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class MnemonicBlockEntity extends BlockEntity {
    List<OutputStep> memory = new ArrayList<>();
    int index = 0;
    int counter = 0;
    Status status = Status.EMPTY;

    public static final int MAX_SIGNAL_DURATION = 600; // Maximum number of ticks allowed in a given sequence step. Being at 0 this long ends the recording.
    public static final int MAX_SEQUENCE_LENGTH = 256; // Maximum number of transitions it can save at once.


    public MnemonicBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.MNEMONIC_BULB_BE_TYPE.value(), pPos, pBlockState);
    }

    public void performTick(Level level, BlockPos pos, BlockState state){
        int input = level.getDirectSignalTo(pos);
        switch(status){
            case EMPTY -> {
                if(input == 0){
                    return;
                }
                index = 0;
                level.setBlock(pos, state.setValue(MnemonicBlock.ACTIVE, true), Block.UPDATE_ALL);
                status = Status.RECORDING;
            }
            case RECORDING -> {
                if (!memory.isEmpty() && memory.getLast().signal == input) {
                    memory.getLast().increment();
                } else {
                    if (memory.size() >= MAX_SEQUENCE_LENGTH) {
                        // This is slow if there are too many things in the list, but that is a rare situation.
                        memory.removeFirst();
                    }
                    memory.add(new OutputStep(input));
                }
                if(state.getValue(MnemonicBlock.CHARGED)){
                    index = 0;
                    level.setBlock(pos, state.setValue(MnemonicBlock.ACTIVE, false), Block.UPDATE_ALL);
                    status = Status.REPLAYING;
                }
                if(memory.getLast().duration > MAX_SIGNAL_DURATION){
                    if(input == 0) {
                        status = Status.DISABLED;
                        level.setBlock(pos, state.setValue(MnemonicBlock.ACTIVE, false), Block.UPDATE_ALL);
                    } else {
                        memory.add(new OutputStep(input));
                    }
                }
            }
            case DISABLED -> {
                if(state.getValue(MnemonicBlock.CHARGED)){
                    index = 0;
                    status = Status.REPLAYING;
                } else if (input > 0) {
                    memory.clear();
                    status = Status.EMPTY;
                }
            }
            case REPLAYING -> {
                if(!state.getValue(MnemonicBlock.CHARGED)){
                    level.setBlock(pos, state.setValue(MnemonicBlock.POWER, 0).setValue(MnemonicBlock.ACTIVE, false), Block.UPDATE_ALL);
                    status = Status.DISABLED;
                }
                index %= memory.size();
                OutputStep output = memory.get(index);
                if(counter < 1){
                    level.setBlock(pos, state.setValue(MnemonicBlock.POWER, output.signal).setValue(MnemonicBlock.ACTIVE, output.signal > 0), Block.UPDATE_ALL);
                    level.playSound(null, pos,
                            output.signal > 0 ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF,
                            SoundSource.BLOCKS, 1.3F, 0.5F + ((output.signal + 1) / 54.0F));
                    level.playSound(null, pos, SoundEvents.MUD_HIT, SoundSource.BLOCKS, 0.1F, 0.7F + ((output.signal + 1) / 54.0F));
                    counter = output.duration;
                    index++;
                }
                counter--;
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MnemonicBlockEntity bulb) {
        bulb.performTick(level, pos, state);
    }

    public boolean hasMemory(){
        return !memory.isEmpty();
    }

    public void stopRecording(){
        if(status.equals(Status.RECORDING)){
            status = Status.DISABLED;
        }
    }

    public static final String MEMORY_TAG = "Memory";
    public static final String INDEX_TAG = "RecordingIndex";
    public static final String COUNTER_TAG = "PlaybackCounter";
    public static final String STATUS_TAG = "Status";

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag memory_list = new ListTag();
        memory_list.addAll(memory.stream().map(OutputStep::save).toList());
        tag.put(MEMORY_TAG, memory_list);
        tag.put(INDEX_TAG, IntTag.valueOf(index));
        tag.put(COUNTER_TAG, IntTag.valueOf(counter));
        tag.put(STATUS_TAG, StringTag.valueOf(status.name()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag memory_list = tag.getList(MEMORY_TAG, Tag.TAG_COMPOUND);
        memory = new ArrayList<>(memory_list.stream().map(OutputStep::load).toList());
        index = tag.getInt(INDEX_TAG);
        counter = tag.getInt(COUNTER_TAG);
        status = Status.valueOf(tag.getString(STATUS_TAG));
    }

    enum Status {
        EMPTY,
        RECORDING,
        REPLAYING,
        DISABLED
    }

    static class OutputStep {
        public int signal;
        public int duration;

        public OutputStep(int signal){
            this.signal = signal;
            this.duration = 1;
        }

        public static OutputStep load(Tag tag){
            if(!(tag instanceof CompoundTag compound)) {
                System.err.println("The mnemonic tag was not a compound tag! Oh no!");
                return null;
            }
            var ret = new OutputStep(compound.getInt("signal"));
            ret.duration = compound.getInt("duration");
            return ret;
        }

        public CompoundTag save(){
            var tag = new CompoundTag();
            tag.put("signal", IntTag.valueOf(signal));
            tag.put("duration", IntTag.valueOf(duration));
            return tag;
        }

        public void increment(){
            this.duration++;
        }

    }
}
