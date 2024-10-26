package dev.hyperlynx.reactive.integration.pehkui;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.fx.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

public class ResizeReactionRenders {
    public static CrucibleBlockEntity acid_based(CrucibleBlockEntity crucible) {
        Level level = crucible.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(crucible.getLevel(), Registration.ACID_BUBBLE_PARTICLE.getType(), crucible.getBlockPos());
        return crucible;
    }

    public static CrucibleBlockEntity verdant_based(CrucibleBlockEntity crucible) {
        Level level = crucible.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.HAPPY_VILLAGER, crucible.getBlockPos());
        return crucible;
    }
}
