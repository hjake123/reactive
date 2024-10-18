package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.function.Function;

// A reaction in which each tick the reactants destroy each other.
public class AnnihilationReaction extends EffectReaction{

    public AnnihilationReaction(String alias, Power p1, Power p2, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render) {
        super(alias, function, render,0);
        reagents.put(p1, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
        reagents.put(p2, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
    }

    @Override
    public void render(Level l, CrucibleBlockEntity crucible) {
        super.render(l, crucible);
        ParticleScribe.drawParticleCrucibleTop(l, ParticleTypes.SMOKE, crucible.getBlockPos(), 0.2F);
    }

    @Override
    public Status conditionsMet(CrucibleBlockEntity crucible) {
        if (super.conditionsMet(crucible) == Status.REACTING){
            if(crucible.getTotalPowerLevel() > WorldSpecificValues.ANNIHILATION_THRESHOLD.get()){
                return Status.REACTING;
            }
            return Status.POWER_TOO_WEAK;
        }
        return Status.STABLE;
    }

    @Override
    public String toString() {
        return super.toString() + " - annihilation reaction";
    }
}
