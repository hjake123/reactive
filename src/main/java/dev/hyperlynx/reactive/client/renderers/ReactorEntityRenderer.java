package dev.hyperlynx.reactive.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.client.renderers.rxn.ReactorRenderer;
import dev.hyperlynx.reactive.entities.ReactorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ReactorEntityRenderer extends EntityRenderer<ReactorEntity> implements ReactorRenderer {
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

        Map<Power, Integer> powers = reactor.reactorData().powers();
        for(Power power : powers.keySet()) {
            float chance = powers.get(power) * 2.0F / reactor.maxPower();
            if(Minecraft.getInstance().level.random.nextFloat() < chance) {
                ParticleScribe.drawExactParticleSphere(reactor.level(),
                        new EnergyParticle.Options(power.getColor(), reactor.position()),
                        reactor.position(), 0.0, 0.4, 1);
            }
        }

        renderReactions(reactor);
        renderCharge(reactor);
    }

    private void renderCharge(ReactorEntity reactor) {
        if(reactor.getElectricCharge() > 0 && reactor.level().random.nextFloat() < 0.03F) {
            ParticleScribe.drawExactParticleSphere(reactor.level(), ParticleTypes.ELECTRIC_SPARK,
                    reactor.position(), 0.0, 0.1, 1);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ResourceLocation getTextureLocation(ReactorEntity entity) {
        return null;
    }

}
