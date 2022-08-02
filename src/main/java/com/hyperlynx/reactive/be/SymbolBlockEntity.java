package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.IPowerBearer;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SymbolBlockEntity extends BlockEntity implements IPowerBearer {

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

    public static void tick(Level level, BlockPos pos, BlockState state, SymbolBlockEntity crucible) {

    }

    @Override
    public boolean addPower(Power p, int amount) {
        return false;
    }

    @Override
    public int getPowerLevel(Power t) {
        return 0;
    }

    @Override
    public int getTotalPowerLevel() {
        return 0;
    }

    @Override
    public boolean expendPower(Power t, int amount) {
        return false;
    }

    @Override
    public void expendAnyPowerExcept(Power immune_power, int amount) {

    }

    @Override
    public void expendPower() {

    }

    @Override
    public Color getCombinedColor(int base) {
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
