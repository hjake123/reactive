package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// This block entity exists only to persist item data while the staff is placed.
// Data is placed by StaffItem by overriding part of BlockItem::place
// Data is read by StaffBlock by overriding playerWillBreak
public class StaffBlockEntity extends BlockEntity {
    public int durability = 0;
    public CompoundTag item_tags;
    public static final String DURABILITY_TAG = "Durability"; // Deprecated since the item tags take care of durability.
    public static final String ITEM_STACK_TAG = "ItemTags";

    public StaffBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.STAFF_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag main_tag) {
        super.saveAdditional(main_tag);
        //main_tag.put(DURABILITY_TAG, IntTag.valueOf(durability));
        if(!(item_tags == null))
            main_tag.put(ITEM_STACK_TAG, item_tags);
    }

    @Override
    public void load(@NotNull CompoundTag main_tag) {
        super.load(main_tag);
        if(main_tag.contains(DURABILITY_TAG))
            durability = main_tag.getInt(DURABILITY_TAG);
        item_tags = main_tag.getCompound(ITEM_STACK_TAG);
    }

}
