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
        float distortion_1 = (float) (Math.sin(time / 50) * 0.03 + 0.95);
        float distortion_2 = (float) (Math.sin(time / 55) * 0.03 + 0.95);
        float distortion_3 = (float) (Math.sin(time / 48) * 0.032 + 0.95);
        float distortion_4 = (float) (Math.sin(time / 52) * 0.032 + 0.95);
        float distortion_5 = (float) (Math.sin((time / 50) + 0.5) * 0.03 + 0.95);
        float distortion_6 = (float) (Math.sin((time / 55) + 0.5) * 0.03 + 0.95);
        float distortion_7 = (float) (Math.sin((time / 48) + 0.5) * 0.032 + 0.95);
        float distortion_8 = (float) (Math.sin((time / 52) + 0.5) * 0.032 + 0.95);

        // SOUTH, NORTH
        this.renderFace(pose, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F , 1.0F, 1.0F, 1.0F,
                distortion_3, distortion_1, distortion_4, distortion_2);
        this.renderFace(pose, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                1.0F, 1.0F, 1.0F, 1.0F);

        // EAST, WEST
        this.renderFace(pose, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F,
                1.0F, 1.0F, distortion_2, distortion_4);
        this.renderFace(pose, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F,
                1.0F, 1.0F, distortion_3, distortion_1);

        // DOWN, UP
        this.renderFace(pose, consumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F,
                distortion_1, 1.0F, distortion_2, 1.0F);
        this.renderFace(pose, consumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F,
                1.0F, distortion_3, 1.0F, distortion_4);
    }

    private void renderFace(Matrix4f pose, VertexConsumer consumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, float ul_distort, float dl_distort, float ur_distort, float dr_distort) {
        consumer.addVertex(pose, adjust(x0, dl_distort), adjust(y0,  dl_distort), adjust(z0, dl_distort));
        consumer.addVertex(pose, adjust(x1,  dr_distort), adjust(y0,  dr_distort), adjust(z1, dr_distort));
        consumer.addVertex(pose, adjust(x1,  ur_distort), adjust(y1,  ur_distort), adjust(z2, ur_distort));
        consumer.addVertex(pose, adjust(x0,  ul_distort), adjust(y1,  ul_distort), adjust(z3, ul_distort));
    }

    private float adjust(float base, float distort_factor){
        float core_dist = base - 0.5F;
        float result_dist = core_dist * distort_factor;
        return result_dist + 0.5F;
    }
}
