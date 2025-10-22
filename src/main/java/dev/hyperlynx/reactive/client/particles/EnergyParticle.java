package dev.hyperlynx.reactive.client.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vec3 target;
    private float speed = 0.05F;
    private final boolean reversed;
    private final boolean orbit;

    protected EnergyParticle(ClientLevel level, double x, double y, double z, Options options, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.target = options.getTarget();
        this.scale(options.getScale());
        this.rCol = options.getColor().x / 255.0F;
        this.gCol = options.getColor().y / 255.0F;
        this.bCol = options.getColor().z / 255.0F;
        this.hasPhysics = false;
        this.reversed = options.reverse_motion;
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

    public static class Options extends DustParticleOptionsBase {
        final float speed;
        final Color color;
        final Vec3 target;
        final boolean reverse_motion;
        final boolean orbit;

        public Options(Color color, Vec3 target) {
            this(0.05F, color, target);
        }

        public Options(float speed, Color color, Vec3 target) {
            this(speed, color, target, false);
        }

        public Options(float speed, Color color, Vec3 target, boolean reverse) {
            this(speed, color, target, reverse, false);
        }

        public Options(float speed, Color color, Vec3 target, boolean reverse, boolean orbit) {
            super(color.toVector3f(), 0.1F);
            this.speed = speed;
            this.color = color;
            this.target = target;
            this.reverse_motion = reverse;
            this.orbit = orbit;
        }

        protected static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                        Codec.FLOAT.fieldOf("speed").forGetter(Options::getSpeed),
                        Color.CODEC.fieldOf("color").forGetter(Options::getColor),
                        Vec3.CODEC.fieldOf("target").forGetter(Options::getTarget),
                        Codec.BOOL.fieldOf("reversed").forGetter(Options::isReversed),
                        Codec.BOOL.fieldOf("orbit").forGetter(Options::isOrbiting)
                ).apply(instance, Options::new)
        );


        @Override
        public @NotNull ParticleType<?> getType() {
            return Registration.ENERGY_PARTICLE_TYPE.get();
        }

        public float getSpeed() {
            return this.speed;
        }

        public Vec3 getTarget() {
            return this.target;
        }

        public boolean isReversed() { return this.reverse_motion; }

        private boolean isOrbiting() {
            return orbit;
        }
    }

    public static class Type extends ParticleType<Options> {
        public Type() {
            super(false);
        }

        @Override
        public Codec<Options> codec() {
            return Options.CODEC.codec();
        }
    }

    public static class Provider implements ParticleProvider<Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull Options options, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            EnergyParticle particle = new EnergyParticle(level, x, y, z, options, sprites);
            particle.speed = options.getSpeed();
            return particle;
        }
    }
}
