package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GatewayBlockEntity extends BlockEntity {
    public GatewayBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.GATEWAY_BLOCK_ENTITY.get(), pos, blockState);
    }
}
