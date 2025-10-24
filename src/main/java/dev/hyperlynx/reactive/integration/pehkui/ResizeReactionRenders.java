package dev.hyperlynx.reactive.integration.pehkui;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

public class ResizeReactionRenders {
    public static void acid_based(Reactor crucible) {
        Level level = crucible.obtainLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(crucible.obtainLevel(), Registration.ACID_BUBBLE_PARTICLE.getType(), crucible.blockPos());
    }

    public static void verdant_based(Reactor crucible) {
        Level level = crucible.obtainLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.HAPPY_VILLAGER, crucible.blockPos());
    }
}
