package com.hyperlynx.reactive.fx;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class StardustParticle extends TextureSheetParticle {
    static final Random RANDOM = new Random();
    private final SpriteSet sprites;

    protected StardustParticle(ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = pSprites;
        this.friction = 0.95F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.hasPhysics = false;
        this.setSpriteFromAge(pSprites);
    }

    // Copied from net.minecraft.client.particle.GlowParticle
    public int getLightColor(float p_172146_) {
        float f = ((float)this.age + p_172146_) / (float)this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(p_172146_);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.setSize(1.2f - ((float) this.age / (float) this.lifetime), 1.2f - ((float) this.age / (float) this.lifetime));
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class StardustParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public StardustParticleProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            var particle = new StardustParticle(pLevel, pX, pY, pZ, 0d, 0d, 0d, this.sprite);
            particle.setColor(1f, 1f, 1f);
            if(RANDOM.nextDouble() < 0.1)
                particle.setParticleSpeed(0, 0, 0);
            else
                particle.setParticleSpeed(RANDOM.nextDouble() * 0.02d - 0.01d, RANDOM.nextDouble() * 0.02d - 0.01d, RANDOM.nextDouble() * 0.02d - 0.01d);
            particle.setLifetime(20);
            return particle;
        }
    }
}
