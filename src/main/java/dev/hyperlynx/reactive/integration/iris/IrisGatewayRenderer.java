package dev.hyperlynx.reactive.integration.iris;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import dev.hyperlynx.reactive.client.renderers.GatewayRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class IrisGatewayRenderer extends GatewayRenderer<GatewayBlockEntity> implements BlockEntityRenderer<GatewayBlockEntity> {
    public IrisGatewayRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    public void render(@NotNull GatewayBlockEntity gateway, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderVolume(gateway, matrix4f, bufferSource.getBuffer(RenderType.entitySolid(TheEndPortalRenderer.END_PORTAL_LOCATION)), partialTick, packedLight, packedOverlay);
    }

    protected void renderVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int light, int overlay) {
        consumer.addVertex(pose, x, y, z).setColor(0xFFFFFFFF).setNormal(0, 0, 0).setUv(0, 0).setOverlay(overlay).setLight(light);
    }
}
