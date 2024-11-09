package dev.hyperlynx.reactive.client.renderers;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionEffects;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Set;

// Just a holder class for the various reaction render methods. Please only call these on the client thank you.
public class ReactionRenders {
    public static void smoke(Reactor reactor) {
        ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.LARGE_SMOKE, reactor.getBlockPos(), 0.3F);
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static void growth(Reactor reactor) {
        ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.HAPPY_VILLAGER, reactor.getBlockPos(), 0.1F);
    }

    // Shoot flames from the crucible!
    public static void flamethrower(Reactor reactor) {
        if(reactor.getLevel() == null) return;

        if(reactor.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
            ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.SOUL_FIRE_FLAME, reactor.getBlockPos(), 0.1F, 0, 0.1, 0);
        }else{
            ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.FLAME, reactor.getBlockPos(), 0.1F, 0, 0.1, 0);
        }
    }

    public static void creation(Reactor reactor){
        Set<BlockPos> points = ReactionEffects.getCreationPoints(reactor.getBlockPos());
        for(BlockPos pos : points){
            if(reactor.getLevel().getBlockState(pos).isAir())
                ParticleScribe.drawParticleSphere(Objects.requireNonNull(reactor.getLevel()), Registration.STARDUST_PARTICLE, pos, 0.5, 1.0, 1);
        }
    }

    public static void ominous(Reactor reactor) {
        ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), ParticleTypes.OMINOUS_SPAWNING, reactor.getBlockPos(), 0.005F);
    }

    public static void acid_based(Reactor reactor) {
        Level level = reactor.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(reactor.getLevel(), Registration.ACID_BUBBLE_PARTICLE.getType(), reactor.getBlockPos());
    }

    public static void verdant_based(Reactor reactor) {
        Level level = reactor.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleCrucibleTop(level, ParticleTypes.HAPPY_VILLAGER, reactor.getBlockPos());
    }
}
