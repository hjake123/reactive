package com.hyperlynx.reactive.fx.renderers;

import com.hyperlynx.reactive.be.SymbolBlockEntity;
import com.hyperlynx.reactive.blocks.SymbolBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;

public class SymbolRenderer implements BlockEntityRenderer<SymbolBlockEntity> {

    private final BlockRenderDispatcher blockRenderDispatcher;
    ItemRenderer itemRenderer;

    public SymbolRenderer(BlockEntityRendererProvider.Context context){
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
        Minecraft minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
    }

    @Override
    public void render(SymbolBlockEntity symbol, float partialTicks, PoseStack pose_stack, MultiBufferSource mbs, int light, int overlay) {
        pose_stack.pushPose();
        if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.UP)){
            pose_stack.translate(0.5, 0.02, 0.5);
            pose_stack.mulPose(Axis.XP.rotationDegrees(90f));
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.DOWN)){
            pose_stack.translate(0.5, 0.98, 0.5);
            pose_stack.mulPose(Axis.XP.rotationDegrees(90f));
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.NORTH)){
            pose_stack.translate(0.5, 0.5, 0.98);
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.SOUTH)){
            pose_stack.translate(0.5, 0.5, 0.02);
        }else if(symbol.getBlockState().getValue(SymbolBlock.FACING).equals(Direction.EAST)){
            pose_stack.translate(0.02, 0.5, 0.5);
            pose_stack.mulPose(Axis.YP.rotationDegrees(90f));
        }else{ // WEST
            pose_stack.translate(0.98, 0.5, 0.5);
            pose_stack.mulPose(Axis.YP.rotationDegrees(90f));
        }

        itemRenderer.renderStatic(symbol.symbol_item.getDefaultInstance(), ItemDisplayContext.FIXED, light, overlay, pose_stack, mbs, symbol.getLevel(), 0);
        pose_stack.popPose();
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }
}
