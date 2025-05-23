package dev.hyperlynx.reactive.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.client.models.HoverQuiltModel;
import dev.hyperlynx.reactive.entites.HoverQuilt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HoverQuiltRenderer extends EntityRenderer<HoverQuilt> {
    public static final ResourceLocation TEXTURE_LOCATION = ReactiveMod.location("textures/entity/entity_quilt.png");
    private HoverQuiltModel model;

    public HoverQuiltRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new HoverQuiltModel(context.bakeLayer(HoverQuiltModel.LAYER_LOCATION));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HoverQuilt entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(@NotNull HoverQuilt quilt, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(quilt, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        model.setupAnim(quilt, partialTick);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180)); // Model was upside down?
        model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entitySolid(TEXTURE_LOCATION)), packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
