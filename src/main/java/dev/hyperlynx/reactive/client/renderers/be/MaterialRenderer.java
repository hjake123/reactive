package dev.hyperlynx.reactive.client.renderers.be;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.be.MaterialBlockEntity;
import dev.hyperlynx.reactive.client.MaterialModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

public class MaterialRenderer implements BlockEntityRenderer<MaterialBlockEntity> {
    private final BlockRenderDispatcher block_renderer;

    public MaterialRenderer(BlockEntityRendererProvider.Context context) {
        block_renderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(MaterialBlockEntity material_block_entity, float partialTick, PoseStack pose, MultiBufferSource source, int light, int overlay) {
        BakedModel model = block_renderer.getBlockModelShaper().getModelManager().getModel(MaterialModels.SALT_MATERIAL);
        pose.pushPose();
        block_renderer.getModelRenderer().renderModel(
                pose.last(),
                source.getBuffer(RenderType.solid()),
                material_block_entity.getBlockState(),
                model,
                1.0F, 1.0F, 1.0F,
                light, overlay,
                ModelData.EMPTY,
                RenderType.solid()
                );
        pose.popPose();
    }

    @Override
    public int getViewDistance() {
        return 512;
    }
}
