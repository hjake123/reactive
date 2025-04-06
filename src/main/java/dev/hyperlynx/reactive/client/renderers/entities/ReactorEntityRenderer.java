package dev.hyperlynx.reactive.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.entites.ReactorEntity;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ReactorEntityRenderer extends EntityRenderer<ReactorEntity> {
    public ReactorEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ReactorEntity reactor, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(reactor, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if(Minecraft.getInstance().isPaused()) {
            return;
        }
        assert Minecraft.getInstance().level != null;

        Map<Power, Integer> powers = reactor.data().powers();
        for(Power power : powers.keySet()) {
            float chance = powers.get(power) / (float) ReactorEntity.MAX_POWER;
            if(Minecraft.getInstance().level.random.nextFloat() < chance) {
                ParticleScribe.drawExactParticleSphere(reactor.level(),
                        new EnergyParticle.Options(0.1F, power.getColor(), reactor.position()),
                        reactor.position(), 0.0, 0.4, 1);
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ResourceLocation getTextureLocation(ReactorEntity entity) {
        return null;
    }
}
