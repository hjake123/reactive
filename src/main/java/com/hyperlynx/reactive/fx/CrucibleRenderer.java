package com.hyperlynx.reactive.fx;

import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.tile.CrucibleBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import com.hyperlynx.reactive.util.Helper;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleBlockEntity> {

    private final BlockRenderDispatcher blockRenderDispatcher;

    public CrucibleRenderer(BlockEntityRendererProvider.Context context){
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    // Inspired by Petal Apothecary rendering in Botania.
    @Override
    public void render(@NotNull CrucibleBlockEntity crucible, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        if(/*crucible.getBlockState().getValue(CrucibleBlock.FULL)*/true) {
            TextureAtlasSprite sprite = this.blockRenderDispatcher.getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
            int color = BiomeColors.getAverageWaterColor(Objects.requireNonNull(crucible.getLevel()), crucible.getBlockPos());
            VertexConsumer buffer = bufferSource.getBuffer(Sheets.translucentCullBlockSheet());
            Helper.renderIcon(poseStack, buffer, sprite, color, 0.7F, combinedOverlay, combinedLight);
        }
        poseStack.popPose();
    }

}
