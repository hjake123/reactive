package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GatewayBlockEntity extends TheEndPortalBlockEntity {
    private int tick_count;

    public GatewayBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.GATEWAY_BE.get(), pos, blockState);
    }

    @Override
    public boolean shouldRenderFace(Direction face) {
        return true;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T gateway) {
        ((GatewayBlockEntity) gateway).tick_count++;
    }

    public float totalTick(float partialTick) {
        return (float) tick_count + partialTick;
    }
}
