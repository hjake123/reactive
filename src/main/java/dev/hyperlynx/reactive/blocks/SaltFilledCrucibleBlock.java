package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SaltFilledCrucibleBlock extends CrucibleShapedBlock{
    public SaltFilledCrucibleBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean super_value = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        level.setBlock(pos, Registration.CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
        return super_value;
    }
}
