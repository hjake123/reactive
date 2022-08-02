package com.hyperlynx.reactive.fx;

import com.hyperlynx.reactive.be.SymbolBlockEntity;
import com.hyperlynx.reactive.blocks.SymbolBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;

public class SymbolRenderer implements BlockEntityRenderer<SymbolBlockEntity> {

    private final BlockRenderDispatcher blockRenderDispatcher;
    ItemRenderer itemRenderer;

    public SymbolRenderer(BlockEntityRendererProvider.Context context){
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
        Minecraft minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
    }

    @Override
    public void render(SymbolBlockEntity symbol, float partialTicks, PoseStack matrix, MultiBufferSource mbs, int light, int overlay) {
        matrix.pushPose();

        if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.UP)){
            matrix.translate(0.5, 0.02, 0.5);
            matrix.mulPose(Vector3f.XP.rotationDegrees(90f));
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.DOWN)){
            matrix.translate(0.5, 0.98, 0.5);
            matrix.mulPose(Vector3f.XP.rotationDegrees(90f));
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.NORTH)){
            matrix.translate(0.5, 0.5, 0.98);
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.SOUTH)){
            matrix.translate(0.5, 0.5, 0.02);
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.EAST)){
            matrix.translate(0.02, 0.5, 0.5);
            matrix.mulPose(Vector3f.YP.rotationDegrees(90f));
        }else{ // WEST
            matrix.translate(0.98, 0.5, 0.5);
            matrix.mulPose(Vector3f.YP.rotationDegrees(90f));
        }

        itemRenderer.renderStatic(symbol.symbol_item.getDefaultInstance(), ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrix, mbs, 0);
        matrix.popPose();
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }
}
