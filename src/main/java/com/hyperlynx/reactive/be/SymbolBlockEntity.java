package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SymbolBlockEntity extends BlockEntity {

    private int tick_counter = 0; // Used for counting active ticks. See tick().

    public Direction facing = Direction.DOWN;
    public Item symbol_item = Items.BARRIER;

    public SymbolBlockEntity(BlockPos pos, BlockState state, Item item) {
        super(Registration.SYMBOL_BE_TYPE.get(), pos, state);
        setItem(item);
    }

    public SymbolBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.SYMBOL_BE_TYPE.get(), pos, state);
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }
    public void setItem(Item item){ this.symbol_item = item; }

    public static void tick(Level level, BlockPos pos, BlockState state, SymbolBlockEntity symbol) {
        symbol.tick_counter++;
        if(symbol.tick_counter >= ConfigMan.COMMON.crucibleTickDelay.get() * 3) {
            symbol.tick_counter = 1;
            if(symbol.symbol_item.getDefaultInstance().is(Registration.COPPER_SYMBOL_ITEM.get()))
                copperTick(level, pos, state, symbol);
            else if (symbol.symbol_item.getDefaultInstance().is(Registration.IRON_SYMBOL_ITEM.get()))
                ironTick(level, pos, state, symbol);
            else if (symbol.symbol_item.getDefaultInstance().is(Registration.GOLD_SYMBOL_ITEM.get()))
                goldTick(level, pos, state, symbol);
        }
    }

    protected static void copperTick(Level level, BlockPos pos, BlockState state, SymbolBlockEntity symbol){

    }

    protected static void ironTick(Level level, BlockPos pos, BlockState state, SymbolBlockEntity symbol){

    }

    protected static void goldTick(Level level, BlockPos pos, BlockState state, SymbolBlockEntity symbol){

    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
