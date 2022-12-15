package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// This block entity exists only to persist durability data while the staff is placed.
// Data is placed by StaffItem by overriding part of BlockItem::place
// Data is read by StaffBlock by overriding playerWillBreak
public class StaffBlockEntity extends BlockEntity {
    public int durability = 1;
    public static final String DURABILITY_TAG = "Durability";

    public StaffBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.STAFF_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag main_tag) {
        super.saveAdditional(main_tag);
        main_tag.put(DURABILITY_TAG, IntTag.valueOf(durability));
    }

    @Override
    public void load(@NotNull CompoundTag main_tag) {
        super.load(main_tag);
        durability = main_tag.getInt(DURABILITY_TAG);
    }

}
