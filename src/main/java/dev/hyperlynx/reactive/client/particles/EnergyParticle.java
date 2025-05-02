package dev.hyperlynx.reactive.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.util.Color;
import dev.hyperlynx.reactive.util.ReactiveVanillaCodecs;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final Vec3 target;
    private float speed = 0.05F;

    protected EnergyParticle(ClientLevel level, double x, double y, double z, Options options, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.target = options.getTarget();
        this.scale(options.getScale());
        this.rCol = options.getColor().red / 255.0F;
        this.gCol = options.getColor().green / 255.0F;
        this.bCol = options.getColor().blue / 255.0F;
        this.hasPhysics = false;
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
        if(this.getPos().closerThan(this.target, speed + 0.01F)){
            this.remove();
        }
    }

    @Override
    public void move(double x, double y, double z) {
        Vec3 pos = this.getPos();
        Vec3 to_target = this.target.subtract(pos);
        Vec3 move_step = to_target.normalize().scale(speed);
        super.move(move_step.x, move_step.y, move_step.z);
    }

    public static class Options extends ScalableParticleOptionsBase {
        float speed = 0.05F;
        Color color;
        Vec3 target;

        protected static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                    Codec.FLOAT.fieldOf("speed").forGetter(Options::getSpeed),
                    Color.CODEC.fieldOf("color").forGetter(Options::getColor),
                    Vec3.CODEC.fieldOf("target").forGetter(Options::getTarget)
                ).apply(instance, Options::new)
        );

        protected static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Options::getSpeed,
                Color.STREAM_CODEC, Options::getColor,
                ReactiveVanillaCodecs.VEC3_STREAM_CODEC, Options::getTarget,
                Options::new
        );

        public Options(Color color, Vec3 target) {
            super(0.1F);
            this.color = color;
            this.target = target;
        }

        public Options(float speed, Color color, Vec3 target) {
            super(0.1F);
            this.speed = speed;
            this.color = color;
            this.target = target;
        }

        @Override
        public @NotNull ParticleType<?> getType() {
            return Registration.ENERGY_PARTICLE_TYPE.get();
        }

        public float getSpeed() {
            return this.speed;
        }

        public Color getColor() {
            return this.color;
        }

        public Vec3 getTarget() {
            return this.target;
        }
    }

    public static class Type extends ParticleType<Options> {
        public Type() {
            super(false);
        }

        @Override
        public MapCodec<Options> codec() {
            return Options.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, Options> streamCodec() {
            return Options.STREAM_CODEC;
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
            particle.setLifetime(200);
            return particle;
        }
    }
}
