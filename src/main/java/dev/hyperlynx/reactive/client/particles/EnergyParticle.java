package dev.hyperlynx.reactive.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.hyperlynx.reactive.particles.EnergyParticleOptions;

public class EnergyParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vec3 target;
    private float speed = 0.05F;
    private final boolean reversed;
    private final boolean orbit;

    protected EnergyParticle(ClientLevel level, double x, double y, double z, EnergyParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.target = options.getTarget();
        this.scale(options.getScale());
        this.rCol = options.getColor().x / 255.0F;
        this.gCol = options.getColor().y / 255.0F;
        this.bCol = options.getColor().z / 255.0F;
        this.hasPhysics = false;
        this.reversed = options.isReversed();
        this.orbit = options.orbit;
        this.setLifetime(reversed || orbit ? 20 : 200);
        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    // Copied from net.minecraft.client.particle.GlowParticle
    @Override
    public int getLightColor(float p_172146_) {
        return LightTexture.pack(15, 15);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        if(!this.reversed && this.getPos().closerThan(this.target, speed + 0.01F)){
            this.remove();
        }
    }

    @Override
    public void move(double x, double y, double z) {
        Vec3 pos = this.getPos();
        Vec3 move_step;
        if(orbit) {
            Vec3 normalized_toward_center = target.subtract(pos).normalize();
            Vec3 normalized_tangent = new Vec3(-normalized_toward_center.z, 0, normalized_toward_center.x);
            move_step = normalized_tangent.scale(speed);
        } else {
            move_step = target.subtract(pos).normalize().scale(speed);
        }

        if(reversed) {
            move_step = move_step.reverse();
        }
        super.move(move_step.x, move_step.y, move_step.z);
    }

    public static class Provider implements ParticleProvider<EnergyParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull EnergyParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            EnergyParticle particle = new EnergyParticle(level, x, y, z, options, sprites);
            particle.speed = options.getSpeed();
            return particle;
        }
    }
}
