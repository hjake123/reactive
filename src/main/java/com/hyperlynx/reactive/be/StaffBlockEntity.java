package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

// This block entity exists only to persist item data while the staff is placed.
// Data is placed by StaffItem by overriding part of BlockItem::place
// Data is read by StaffBlock by overriding playerWillBreak
public class StaffBlockEntity extends BlockEntity {
    public ItemStack stack;
    private final String ITEM_STACK_TAG = "Stack";

    public StaffBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.STAFF_BE.get(), pos, state);
        stack = state.getBlock().asItem().getDefaultInstance();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag main_tag, HolderLookup.@NotNull Provider registry_provider) {
        super.saveAdditional(main_tag, registry_provider);
        main_tag.put(ITEM_STACK_TAG, stack.save(registry_provider));
    }

    @Override
    protected void loadAdditional(CompoundTag main_tag, HolderLookup.Provider registry_provider) {
        super.loadAdditional(main_tag, registry_provider);
        Optional<ItemStack> possible_stack = ItemStack.parse(registry_provider, main_tag.get(ITEM_STACK_TAG));
        possible_stack.ifPresent(itemStack -> stack = itemStack);
    }
}
