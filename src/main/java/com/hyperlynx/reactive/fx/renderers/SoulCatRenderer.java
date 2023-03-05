package com.hyperlynx.reactive.fx.renderers;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.entities.SoulCat;
import com.hyperlynx.reactive.fx.models.SoulCatModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SoulCatRenderer extends MobRenderer<SoulCat, SoulCatModel> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ReactiveMod.MODID, "textures/entity/soulcat.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);

    public SoulCatRenderer(EntityRendererProvider.Context context) {
        super(context, new SoulCatModel(context.bakeLayer(SoulCatModel.LAYER_LOCATION)), 1.0f);
        context.bakeLayer(SoulCatModel.LAYER_LOCATION);
        this.shadowRadius = 0.2f;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(SoulCat p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
        return RENDER_TYPE;
    }

    @Override
    public ResourceLocation getTextureLocation(SoulCat cat) {
        return TEXTURE_LOCATION;
    }
}
