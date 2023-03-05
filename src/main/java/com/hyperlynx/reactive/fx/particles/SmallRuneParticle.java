package com.hyperlynx.reactive.fx.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallRuneParticle extends RuneParticle{
    protected SmallRuneParticle(ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pSprites);
        this.base_size = 0.5F;
    }

    public static class SmallRuneParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public SmallRuneParticleProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            var particle = new SmallRuneParticle(pLevel, pX, pY, pZ, 0d, 0d, 0d, this.sprite);
            particle.setColor(RANDOM.nextFloat(0.8F, 1F), RANDOM.nextFloat(0.6F, 1F), RANDOM.nextFloat(0.8F, 1F));
            particle.setParticleSpeed(0, -0.01, 0);
            particle.setLifetime(RANDOM.nextInt(20, 30));
            return particle;
        }
    }
}
