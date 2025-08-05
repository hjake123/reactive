package dev.hyperlynx.reactive.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class AcidBubbleParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    @SuppressWarnings("SameParameterValue")
    protected AcidBubbleParticle(ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = pSprites;
        this.friction = 0.95F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.hasPhysics = false;
        this.setSpriteFromAge(pSprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record AcidBubbleParticleProvider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType> {

            @Override
            public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
                var particle = new AcidBubbleParticle(pLevel, pX, pY, pZ, 0d, 0d, 0d, this.sprite);
                particle.setColor(1f, 1f, 1f);
                particle.setParticleSpeed(0, 0, 0);
                particle.setLifetime(10);
                particle.setSize(0.6F, 0.6F);
                return particle;
            }
        }
}
