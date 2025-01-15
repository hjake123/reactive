package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GatewayBlock extends Block implements Portal, EntityBlock {
    public GatewayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new GatewayBlockEntity(pos, state);
    }

    @Override
    public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
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

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
