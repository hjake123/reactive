package dev.hyperlynx.reactive.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

// Duplicated from TheEndPortalRenderer
public class GatewayRenderer<T extends GatewayBlockEntity> extends TheEndPortalRenderer<T> {
    public GatewayRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }

    protected float getOffsetUp() {
        return 1F;
    }

    protected float getOffsetDown() {
        return 0F;
    }
}
