package dev.hyperlynx.reactive.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

// Adapted from TheEndPortalRenderer
public class GatewayRenderer<T extends GatewayBlockEntity> implements BlockEntityRenderer<T> {
    BlockEntityRendererProvider.Context context;

    public GatewayRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    public void render(@NotNull T gateway, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderCube(gateway, matrix4f, bufferSource.getBuffer(RenderType.END_GATEWAY), partialTick);
    }

    private void renderCube(T gateway, Matrix4f pose, VertexConsumer consumer, float partialTick) {
        double time = gateway.totalTick(partialTick);
        float distortion = (float) (Math.sin(time / 50) * 0.03 + 0.04);

        // SOUTH, NORTH
        this.renderFace(pose, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F , 1.0F, 1.0F, 1.0F);
        this.renderFace(pose, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        // EAST, WEST
        this.renderFace(pose, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F);
        this.renderFace(pose, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);

        // DOWN, UP
        this.renderFace(pose, consumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F);
        this.renderFace(pose, consumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F);
    }

    private void renderFace(Matrix4f pose, VertexConsumer consumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3) {
        consumer.addVertex(pose, x0, y0, z0);
        consumer.addVertex(pose, x1, y0, z1);
        consumer.addVertex(pose, x1, y1, z2);
        consumer.addVertex(pose, x0, y1, z3);
    }
}
