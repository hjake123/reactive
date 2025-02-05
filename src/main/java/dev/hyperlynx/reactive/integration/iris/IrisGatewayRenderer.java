package dev.hyperlynx.reactive.integration.iris;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.hyperlynx.reactive.be.GatewayBlockEntity;
import dev.hyperlynx.reactive.fx.renderers.GatewayRenderer;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.uniforms.SystemTimeUniforms;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

// Adapted from Iris source code. (https://github.com/IrisShaders/Iris/blob/multiloader-new/common/src/main/java/net/irisshaders/iris/mixin/MixinTheEndPortalRenderer.java)
// Iris is under a permissive license, so I hope that this notice will be enough!
// If you have any concerns, please open an issue.
public class IrisGatewayRenderer extends GatewayRenderer<GatewayBlockEntity> implements BlockEntityRenderer<GatewayBlockEntity> {
    private static final float RED = 0.075f;
    private static final float GREEN = 0.15f;
    private static final float BLUE = 0.2f;

    public IrisGatewayRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private boolean isNotUsingShaders() {
        return Iris.getCurrentPack().isEmpty();
    }

    public void render(GatewayBlockEntity gateway, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        if (isNotUsingShaders()) {
            super.render(gateway, partialTick, poseStack, multiBufferSource, light, overlay);
            return;
        }

        // POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entitySolid(TheEndPortalRenderer.END_PORTAL_LOCATION));

        PoseStack.Pose pose = poseStack.last();

        // animation with a period of 100 seconds.
        // note that texture coordinates are wrapping, not clamping.
        float progress = (SystemTimeUniforms.TIMER.getFrameTimeCounter() * 0.01f) % 1f;

        renderVolume(gateway, new IrisGatewayRendererContext(vertexConsumer, pose, progress, light, overlay), partialTick);
    }

    @Override
    protected void renderFace(GatewayRenderContext context, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, float ul_distort, float dl_distort, float ur_distort, float dr_distort) {
        if(context instanceof IrisGatewayRendererContext iris_context){
            iris_context.calculateNormal(new Vector3f(x0, y0, z0), new Vector3f(x1, y0, z1), new Vector3f(x1, y1, z2));
        }
        super.renderFace(context, x0, x1, y0, y1, z0, z1, z2, z3, ul_distort, dl_distort, ur_distort, dr_distort);
    }

    public static class IrisGatewayRendererContext extends GatewayRenderContext {
        int light;
        int overlay;
        float progress;
        int count;
        Vector3f normal;

        protected IrisGatewayRendererContext(VertexConsumer consumer, PoseStack.Pose pose, float progress, int light, int overlay){
            super(consumer, pose);
            this.light = light;
            this.progress = progress;
            this.overlay = overlay;
            count = 0;
        }

        // Based on the pseudocode here: (https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal)
        protected void calculateNormal(Vector3f a, Vector3f b, Vector3f c){
            var u = b.sub(a);
            var v = c.sub(a);

            float x = (u.y * v.z) - (u.z * v.y);
            float y = (u.z * v.x) - (u.x * v.z);
            float z = (u.x * v.y) - (u.y * v.x);

            normal = new Vector3f(x, y, z);
        }

        @Override
        protected void renderVertex(float x, float y, float z) {
            float v_offset = count < 2 ? 0.2F : 0.0F;
            float v1_offset = count % 2 == 0 ? 0.0F: 0.2F;
            consumer.vertex(pose.pose(), x, y, z)
                    .color(RED, GREEN, BLUE, 0xFF)
                    .uv(v_offset + progress, v1_offset + progress)
                    .overlayCoords(overlay)
                    .uv2(light)
                    .normal(normal.x, normal.y, normal.z)
                    .endVertex();
            count = (count + 1) % 4;
        }
    }
}
