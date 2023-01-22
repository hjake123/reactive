package com.hyperlynx.reactive.fx;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmallBlackRuneParticle extends RuneParticle{
    protected SmallBlackRuneParticle(ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pSprites);
        this.base_size = 0.5F;
        this.speedUpWhenYMotionIsBlocked = false;
    }

    public static class SmallBlackRuneParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public SmallBlackRuneParticleProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            var particle = new SmallBlackRuneParticle(pLevel, pX, pY, pZ, 0d, 0d, 0d, this.sprite);
            particle.setColor(0, 0, 0);
            particle.setParticleSpeed(0, 0, 0);
            particle.setLifetime(RANDOM.nextInt(50, 100));
            return particle;
        }
    }
}
