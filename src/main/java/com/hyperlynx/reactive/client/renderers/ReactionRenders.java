package com.hyperlynx.reactive.client.renderers;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.rxn.ReactionEffects;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;

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
        Set<BlockPos> points = ReactionEffects.get_creation_points(c.getBlockPos());
        for(BlockPos pos : points){
            if(c.getLevel().getBlockState(pos).isAir())
                ParticleScribe.drawParticleSphere(Objects.requireNonNull(c.getLevel()), Registration.STARDUST_PARTICLE, pos, 0.5, 1.0, 1);
        }
        return c;
    }
}
