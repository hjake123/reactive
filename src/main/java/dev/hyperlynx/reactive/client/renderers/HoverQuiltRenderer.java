package dev.hyperlynx.reactive.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.client.models.HoverQuiltModel;
import dev.hyperlynx.reactive.entities.HoverQuilt;
import dev.hyperlynx.reactive.net.quilt.HoverQuiltHeightMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class HoverQuiltRenderer extends EntityRenderer<HoverQuilt> implements RenderLayerParent<HoverQuilt, HoverQuiltModel> {
    public static final ResourceLocation TEXTURE_LOCATION = ReactiveMod.location("textures/entity/entity_quilt.png");
    private final HoverQuiltModel model;

    public HoverQuiltRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new HoverQuiltModel(context.bakeLayer(HoverQuiltModel.LAYER_LOCATION));
    }

    @Override
    public @NotNull HoverQuiltModel getModel() {
        return model;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HoverQuilt entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(@NotNull HoverQuilt quilt, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(quilt, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        model.setupAnim(quilt, quilt.animation_timer + partialTick);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180)); // Model was upside down?
        poseStack.translate(0, -1.35, 0); // Model is far from the hitbox?
        model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entitySolid(TEXTURE_LOCATION)), packedLight, OverlayTexture.NO_OVERLAY, 0, 0, 0, 0);
        poseStack.popPose();
    }

    public static void handleHeightPacket(HoverQuiltHeightMessage payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            assert Minecraft.getInstance().level != null;
            HoverQuilt.handleHeightUpdate(payload, Minecraft.getInstance().level);
        });
    }
}
