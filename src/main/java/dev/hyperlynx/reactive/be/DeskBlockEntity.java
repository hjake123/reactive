package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.items.MaterialItem;
import dev.hyperlynx.reactive.menu.DeskMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DeskBlockEntity extends BaseContainerBlockEntity implements IItemHandlerModifiable {
    private @NotNull ItemStack stack = ItemStack.EMPTY;

    public DeskBlockEntity(BlockPos pos, BlockState blockState) {
        super(ReactiveBlockEntityTypes.DESK.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.reactive.desk");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return NonNullList.of(ItemStack.EMPTY, stack);
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        stack = items.get(0);
        if(items.size() > 1) {
            throw new IllegalArgumentException("Desk block entity cannot accept more then one ItemStack.");
        }
        this.setChanged();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new DeskMenu(containerId, inventory);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("stack")) {
            stack = ItemStack.parseOptional(registries, tag.getCompound("stack"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(!stack.isEmpty()) {
            tag.put("stack", stack.save(new CompoundTag()));
        }
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
    public ItemStack getItem(int pSlot) {
        return null;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return null;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if(slot == 0) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack_to_insert, boolean simulate) {
        if(!isItemValid(slot, stack_to_insert)) {
            return stack_to_insert;
        }
        if(stack.isEmpty()) {
            stack = stack_to_insert.copy();
            return ItemStack.EMPTY;
        }
        if(!Objects.equals(MaterialItem.getMaterialId(stack_to_insert), MaterialItem.getMaterialId(stack))) {
            return stack_to_insert;
        }
        int number_to_move = Math.min(stack.getMaxStackSize() - stack.getCount(), stack_to_insert.getCount());
        ItemStack after_items_depleted = stack_to_insert.copyWithCount(stack_to_insert.getCount() - number_to_move);
        if(!simulate) {
            stack.setCount(stack.getCount() + number_to_move);
        }
        return after_items_depleted;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot != 0) {
            return ItemStack.EMPTY;
        }
        if(amount > stack.getCount()) {
            return ItemStack.EMPTY;
        }
        ItemStack extracted = stack.copyWithCount(amount);
        if(!simulate) {
            stack.shrink(amount);
            if(stack.getCount() == 0) {
                stack = ItemStack.EMPTY;
            }
        }
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot == 0 && stack.is(Registration.MATERIAL_ITEM.get());
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if(slot != 0) {
            return;
        }
        setItems(NonNullList.of(ItemStack.EMPTY, stack));
    }

    @Override
    public void clearContent() {

    }
}
