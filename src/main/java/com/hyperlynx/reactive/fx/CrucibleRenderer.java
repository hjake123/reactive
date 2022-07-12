package com.hyperlynx.reactive.fx;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {

    private final BlockRenderDispatcher blockRenderDispatcher;

    public CrucibleRenderer(BlockEntityRendererProvider.Context context){
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    private TextureAtlasSprite getSprite(CrucibleBlockEntity crucible){
        int theshold = CrucibleBlockEntity.CRUCIBLE_MAX_POWER/2;

        if(crucible.getPowerLevel(Registration.CURSE_POWER.get()) > theshold || crucible.getPowerLevel(Registration.WARP_POWER.get()) > theshold){
            return this.blockRenderDispatcher.getBlockModel(Registration.DUMMY_NOISE_WATER.get().defaultBlockState()).getParticleIcon(ModelData.EMPTY);
        }
        else if(crucible.getPowerLevel(Registration.MIND_POWER.get()) > theshold || crucible.getPowerLevel(Registration.LIGHT_POWER.get()) > theshold){
            return this.blockRenderDispatcher.getBlockModel(Registration.DUMMY_MAGIC_WATER.get().defaultBlockState()).getParticleIcon(ModelData.EMPTY);
        }

        return this.blockRenderDispatcher.getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public void render(@NotNull CrucibleBlockEntity crucible, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        // 0.5625 is the 'full' water level.
        poseStack.translate(0, 0.5625, 0);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90f));
        if(crucible.getBlockState().getValue(CrucibleBlock.FULL)) {
            TextureAtlasSprite sprite = getSprite(crucible);
            Color color = crucible.getCombinedColor(BiomeColors.getAverageWaterColor(Objects.requireNonNull(crucible.getLevel()), crucible.getBlockPos()));
            VertexConsumer buffer = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());
            renderIcon(poseStack, buffer, sprite, color, crucible.getOpacity(), combinedOverlay, combinedLight);
        }
        poseStack.popPose();
    }

    // Stolen from Botania's repository here: (https://github.com/VazkiiMods/Botania/blob/9d468aadc9293ea8652092bc4caf804b61fc04c9/Xplat/src/main/java/vazkii/botania/client/render/tile/RenderTileAltar.java)
    public static void renderIcon(PoseStack ms, VertexConsumer builder, TextureAtlasSprite sprite, Color color, float alpha, int overlay, int light) {
        Matrix4f mat = ms.last().pose();
        // Due to previous rotation, Y and Z are switched.
        builder.vertex(mat, 0.2f, 0.8f, 0).color(color.red, color.green, color.blue, (int) (alpha * 255F)).uv(sprite.getU0(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(mat, 0.8f, 0.8f, 0).color(color.red, color.green, color.blue, (int) (alpha * 255F)).uv(sprite.getU1(), sprite.getV1()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(mat, 0.8f, 0.2f, 0).color(color.red, color.green, color.blue, (int) (alpha * 255F)).uv(sprite.getU1(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
        builder.vertex(mat, 0.2f, 0.2f, 0).color(color.red, color.green, color.blue, (int) (alpha * 255F)).uv(sprite.getU0(), sprite.getV0()).overlayCoords(overlay).uv2(light).normal(0, 0, 1).endVertex();
    }

}
