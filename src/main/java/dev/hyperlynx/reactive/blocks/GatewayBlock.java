package dev.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;

public class GatewayBlock extends Block implements Portal, EntityBlock {
    public GatewayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        return Portal.super.getPortalTransitionTime(level, entity);
    }

    @Override
    public @Nullable DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
        return null;
    }

    @Override
    public Transition getLocalTransition() {
        return Portal.super.getLocalTransition();
    }
}
