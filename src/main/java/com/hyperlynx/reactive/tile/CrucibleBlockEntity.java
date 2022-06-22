package com.hyperlynx.reactive.tile;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CrucibleBlockEntity extends BlockEntity {
    public CrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.CRUCIBLE_BE_TYPE.get(), pos, state);
    }

    public float getWaterRed() {
        return 1.0F;
    }
    public float getWaterGreen() {
        return 1.0F;
    }
    public float getWaterBlue() {
        return 1.0F;
    }
    public float getWaterOpacity() {
        return 0.7F;
    }
}
