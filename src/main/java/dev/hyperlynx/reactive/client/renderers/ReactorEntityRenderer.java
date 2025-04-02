package dev.hyperlynx.reactive.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.entites.ReactorEntity;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ReactorEntityRenderer extends EntityRenderer<ReactorEntity> {
    public ReactorEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ReactorEntity reactor, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(reactor, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        ParticleScribe.drawExactParticleSphere(reactor.level(),
                new EnergyParticle.Options(0.1F, new Color(0xFFEEFF), reactor.position()),
                reactor.position(), 0.0, 0.5, 1);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ResourceLocation getTextureLocation(ReactorEntity entity) {
        return null;
    }
}
