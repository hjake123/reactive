package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RunestoneBlock extends Block {
    public RunestoneBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return WorldSpecificValue.get((Level) level, "rune_power", 0.45F, 0.67F);
    }

}
