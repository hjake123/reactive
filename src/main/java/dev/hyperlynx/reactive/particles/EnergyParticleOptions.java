package dev.hyperlynx.reactive.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EnergyParticleOptions extends DustParticleOptionsBase {
        final float speed;
        final Color color;
        final Vec3 target;
        final boolean reverse_motion;
        public final boolean orbit;

        public EnergyParticleOptions(Color color, Vec3 target) {
            this(0.05F, color, target);
        }

        public EnergyParticleOptions(float speed, Color color, Vec3 target) {
            this(speed, color, target, false);
        }

        public EnergyParticleOptions(float speed, Color color, Vec3 target, boolean reverse) {
            this(speed, color, target, reverse, false);
        }

        public EnergyParticleOptions(float speed, Color color, Vec3 target, boolean reverse, boolean orbit) {
            super(color.toVector3f(), 0.1F);
            this.speed = speed;
            this.color = color;
            this.target = target;
            this.reverse_motion = reverse;
            this.orbit = orbit;
        }

        protected static final MapCodec<EnergyParticleOptions> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                        Codec.FLOAT.fieldOf("speed").forGetter(EnergyParticleOptions::getSpeed),
                        Color.CODEC.fieldOf("color").forGetter(EnergyParticleOptions::getHyperColor),
                        Vec3.CODEC.fieldOf("target").forGetter(EnergyParticleOptions::getTarget),
                        Codec.BOOL.fieldOf("reversed").forGetter(EnergyParticleOptions::isReversed),
                        Codec.BOOL.fieldOf("orbit").forGetter(EnergyParticleOptions::isOrbiting)
                ).apply(instance, EnergyParticleOptions::new)
        );


        @Override
        public @NotNull ParticleType<?> getType() {
            return Registration.ENERGY_PARTICLE_TYPE.get();
        }

        private Color getHyperColor() { return color; }

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

        public static class Type extends ParticleType<EnergyParticleOptions> {
            public Type() {
                super(false, DESERIALIZER);
            }

            @Override
            public @NotNull Codec<EnergyParticleOptions> codec() {
                return EnergyParticleOptions.CODEC.codec();
            }
        }

        public static final ParticleOptions.Deserializer<EnergyParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
            @Override
            public EnergyParticleOptions fromCommand(ParticleType<EnergyParticleOptions> type, StringReader reader) throws CommandSyntaxException {
                try {
                    return new EnergyParticleOptions(reader.readFloat(), new Color(reader.readInt()), new Vec3(reader.readDouble(), reader.readDouble(), reader.readDouble()),
                            reader.readBoolean(), reader.readBoolean());
                } catch (Exception e) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create(e);
                }
            }

            @Override
            public EnergyParticleOptions fromNetwork(ParticleType<EnergyParticleOptions> type, FriendlyByteBuf buf) {
                return new EnergyParticleOptions(buf.readFloat(), new Color(buf.readInt()), new Vec3(buf.readVector3f()), buf.readBoolean(), buf.readBoolean());
            }
        };
}
