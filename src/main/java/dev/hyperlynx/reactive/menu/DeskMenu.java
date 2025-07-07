package dev.hyperlynx.reactive.menu;

import dev.hyperlynx.reactive.registration.ReactiveBlocks;
import dev.hyperlynx.reactive.registration.ReactiveMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class DeskMenu extends AbstractContainerMenu {
    Inventory player_inventory;
    ContainerLevelAccess access;

    private static final int CONTAINER_SLOT_INDEX = 0;

    public DeskMenu(int containerId, Inventory player_inventory) {
        this(containerId, player_inventory, new ItemStackHandler(1), ContainerLevelAccess.NULL);
    }

    public DeskMenu(int containerId, Inventory player_inventory, IItemHandler desk_inventory, ContainerLevelAccess access) {
        super(ReactiveMenus.DESK_MENU.get(), containerId);
        this.player_inventory = player_inventory;
        this.access = access;
        this.addSlot(new SlotItemHandler(desk_inventory, CONTAINER_SLOT_INDEX, 0, 0));
        // Copied from BrewingStandMenu.java
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(player_inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(player_inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(access, player, ReactiveBlocks.DESK.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int from_slot_index) {
        ItemStack to_stack = ItemStack.EMPTY;
        Slot from_slot = this.slots.get(from_slot_index);

        if(from_slot.hasItem()) {
            ItemStack from_stack = from_slot.getItem();

            if(from_slot_index == CONTAINER_SLOT_INDEX) {
                if(!moveItemStackTo(from_stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!moveItemStackTo(from_stack, 0, 1, false)) {
                if(!moveItemStackTo(from_stack, 27,37, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if(from_stack.isEmpty()) {
                from_slot.set(ItemStack.EMPTY);
            } else {
                from_slot.setChanged();
            }
        }

        return to_stack;
    }

}
