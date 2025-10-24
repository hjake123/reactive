package dev.hyperlynx.reactive.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VirtualCraftingContainer implements CraftingContainer {
    private final List<ItemStack> items = new ArrayList<>(9);
    public final boolean all_empty;

    public VirtualCraftingContainer(CraftingContainer other) {
        boolean all_empty = true;
        for(ItemStack stack : other.getItems()) {
            items.add(stack.copy());
            if(!stack.isEmpty()) {
                all_empty = false;
            }
        }
        this.all_empty = all_empty;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        throw new RuntimeException("Not implemented VirtualCraftingContainer::removeItem");
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        throw new RuntimeException("Not implemented VirtualCraftingContainer::removeItemNoUpdate");

    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        items.set(pSlot, pStack);
    }

    @Override
    public void setChanged() {
        // NO-OP
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public void fillStackedContents(StackedContents pContents) {
        throw new RuntimeException("Not implemented VirtualCraftingContainer::fillStackedContents");
    }
}
