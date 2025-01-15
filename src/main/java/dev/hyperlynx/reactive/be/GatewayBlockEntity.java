package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GatewayBlockEntity extends TheEndPortalBlockEntity {
    public GatewayBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.GATEWAY_BE.get(), pos, blockState);
    }

    @Override
    public boolean shouldRenderFace(Direction face) {
        return true;
    }
}
