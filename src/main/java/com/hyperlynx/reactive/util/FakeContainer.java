package com.hyperlynx.reactive.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

// A fake container wrapper for an itemstack.
public class FakeContainer implements Container {
    ItemStack i;

    public FakeContainer(ItemStack i){
        this.i = i;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return i;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return i;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return i;
    }

    @Override
    public void setItem(int p_18944_, ItemStack newI) {
        i = newI;
    }

    @Override
    public void setChanged() {
        // Doesn't work...
    }

    @Override
    public boolean stillValid(Player p) {
        return true;
    }

    @Override
    public void clearContent() {
        // Doesn't work...
    }
}