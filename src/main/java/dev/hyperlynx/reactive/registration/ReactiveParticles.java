package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ReactiveMod.MODID);

    public static final SimpleParticleType STARDUST_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STARDUST_PARTICLE_TYPE = PARTICLES.register("stardust",
            () -> STARDUST_PARTICLE);

    public static final SimpleParticleType RUNE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RUNE_PARTICLE_TYPE = PARTICLES.register("runes",
            () -> RUNE_PARTICLE);

    public static final SimpleParticleType SMALL_RUNE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMALL_RUNE_PARTICLE_TYPE = PARTICLES.register("small_runes",
            () -> SMALL_RUNE_PARTICLE);

    public static final SimpleParticleType SMALL_BLACK_RUNE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMALL_BLACK_RUNE_PARTICLE_TYPE = PARTICLES.register("small_black_runes",
            () -> SMALL_BLACK_RUNE_PARTICLE);

    public static final SimpleParticleType ACID_BUBBLE_PARTICLE = new SimpleParticleType(false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_BUBBLE_PARTICLE_TYPE = PARTICLES.register("acid_bubble",
            () -> ACID_BUBBLE_PARTICLE);

    public static final ParticleType<EnergyParticle.Options> ENERGY_PARTICLE = new EnergyParticle.Type();
    public static final DeferredHolder<ParticleType<?>, ParticleType<EnergyParticle.Options>> ENERGY_PARTICLE_TYPE = PARTICLES.register("energy",
            () -> ENERGY_PARTICLE);
}
