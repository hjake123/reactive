package com.hyperlynx.reactive.fx.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class AcidBubbleParticle extends TextureSheetParticle {
    static final Random RANDOM = new Random();
    private final SpriteSet sprites;

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

    public static class AcidBubbleParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public AcidBubbleParticleProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
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
