package dev.hyperlynx.reactive.client.renderers;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionEffects;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Set;

// Just a holder class for the various reaction render methods. Please only call these on the client thank you.
public class ReactionRenders {
    public static CrucibleBlockEntity smoke(CrucibleBlockEntity c) {
        ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.LARGE_SMOKE, c.getBlockPos(), 0.3F);
        return c;
    }

    // Causes nearby bonemeal-ables to be fertilized occasionally.
    public static CrucibleBlockEntity growth(CrucibleBlockEntity c) {
        ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.HAPPY_VILLAGER, c.getBlockPos(), 0.1F);
        return c;
    }

    // Shoot flames from the crucible!
    public static CrucibleBlockEntity flamethrower(CrucibleBlockEntity c) {
        if(c.getLevel() == null) return c;

        if(c.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
            ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.SOUL_FIRE_FLAME, c.getBlockPos(), 0.1F, 0, 0.1, 0);
        }else{
            ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.FLAME, c.getBlockPos(), 0.1F, 0, 0.1, 0);
        }

        return c;
    }

    public static CrucibleBlockEntity creation(CrucibleBlockEntity c){
        Set<BlockPos> points = ReactionEffects.getCreationPoints(c.getBlockPos());
        for(BlockPos pos : points){
            if(c.getLevel().getBlockState(pos).isAir())
                ParticleScribe.drawParticleSphere(Objects.requireNonNull(c.getLevel()), Registration.STARDUST_PARTICLE, pos, 0.5, 1.0, 1);
        }
        return c;
    }

    public static CrucibleBlockEntity ominous(CrucibleBlockEntity c) {
        ParticleScribe.drawParticleCrucibleTop(c.getLevel(), ParticleTypes.OMINOUS_SPAWNING, c.getBlockPos(), 0.005F);
        return c;
    }

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
