package dev.hyperlynx.reactive.fx.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

// Adapted from TheEndPortalRenderer
public class GatewayRenderer<T extends GatewayBlockEntity> implements BlockEntityRenderer<T> {
    BlockEntityRendererProvider.Context context;

    public GatewayRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    public void render(@NotNull T gateway, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderVolume(gateway, new GatewayRenderContext(bufferSource.getBuffer(RenderType.endGateway()), poseStack.last()), partialTick);
    }

    protected void renderVolume(T gateway, GatewayRenderContext context, float partialTick) {
        double time = gateway.totalTick(partialTick);
        float amplitude = 0.12F;
        float distortion_1 = (float) (Math.sin(time / 50) * amplitude + 0.95);
        float distortion_2 = (float) (Math.sin(time / 55) * amplitude + 0.95);
        float distortion_3 = (float) (Math.sin(time / 48) * amplitude + 0.95);
        float distortion_4 = (float) (Math.sin(time / 52) * amplitude + 0.95);
        float distortion_5 = (float) (Math.sin((time / 50) + 0.5) * amplitude + 0.95);
        float distortion_6 = (float) (Math.sin((time / 55) + 0.5) * amplitude + 0.95);
        float distortion_7 = (float) (Math.sin((time / 48) + 0.5) * amplitude + 0.95);
        float distortion_8 = (float) (Math.sin((time / 52) + 0.5) * amplitude + 0.95);

        // SOUTH, NORTH
        this.renderFace(context, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F , 1.0F, 1.0F, 1.0F,
                distortion_3, distortion_1, distortion_4, distortion_2);
        this.renderFace(context, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
                distortion_7, distortion_5, distortion_6, distortion_8);

        // EAST, WEST
        this.renderFace(context, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F,
                distortion_6, distortion_8, distortion_2, distortion_4);
        this.renderFace(context, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F,
                distortion_5, distortion_7, distortion_3, distortion_1);

        // DOWN, UP
        this.renderFace(context, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F,
                distortion_1, distortion_7, distortion_2, distortion_6);
        this.renderFace(context, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F,
                distortion_5, distortion_3, distortion_8, distortion_4);
    }

    protected void renderFace(GatewayRenderContext context, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, float ul_distort, float dl_distort, float ur_distort, float dr_distort) {
        context.renderVertex(adjust(x0, dl_distort), adjust(y0,  dl_distort), adjust(z0, dl_distort));
        context.renderVertex(adjust(x1,  dr_distort), adjust(y0,  dr_distort), adjust(z1, dr_distort));
        context.renderVertex(adjust(x1,  ur_distort), adjust(y1,  ur_distort), adjust(z2, ur_distort));
        context.renderVertex(adjust(x0,  ul_distort), adjust(y1,  ul_distort), adjust(z3, ul_distort));
    }

    protected float adjust(float base, float distort_factor){
        float core_dist = base - 0.5F;
        float result_dist = core_dist * distort_factor;
        return result_dist + 0.5F;
    }

    public static class GatewayRenderContext {
        protected VertexConsumer consumer;
        protected PoseStack.Pose pose;

        protected GatewayRenderContext(VertexConsumer consumer, PoseStack.Pose pose) {
            this.consumer = consumer;
            this.pose = pose;
        }

        protected void renderVertex(float x, float y, float z) {
            this.consumer.vertex(this.pose.pose(), x, y, z).endVertex();
        }
    }
}
