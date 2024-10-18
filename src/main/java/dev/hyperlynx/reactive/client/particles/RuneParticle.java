package dev.hyperlynx.reactive.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class RuneParticle extends TextureSheetParticle {
    static final Random RANDOM = new Random();
    private final SpriteSet sprites;
    protected float base_size;

    protected RuneParticle(ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = pSprites;
        this.friction = 0.95F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.hasPhysics = false;
        this.age = RANDOM.nextInt(0, 5);
        this.setSpriteFromAge(sprites);
        this.base_size = 1.2F;
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
        this.setSize(base_size - ((float) this.age / (float) this.lifetime), base_size - ((float) this.age / (float) this.lifetime));
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class RuneParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public RuneParticleProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            var particle = new RuneParticle(pLevel, pX, pY, pZ, 0d, 0d, 0d, this.sprite);
            particle.setColor(RANDOM.nextFloat(0.8F, 1F), RANDOM.nextFloat(0.6F, 1F), RANDOM.nextFloat(0.8F, 1F));
            particle.setParticleSpeed(0, -0.01, 0);
            particle.setLifetime(RANDOM.nextInt(20, 50));
            return particle;
        }
    }
}
