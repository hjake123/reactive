package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

// A reaction in which each tick the reactants destroy each other.
public class AnnihilationReaction extends EffectReaction{

    public AnnihilationReaction(String alias, Power p1, Power p2, Consumer<Reactor> function, Consumer<Reactor> render) {
        super(alias, function, render,0);
        reagents.put(p1, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
        reagents.put(p2, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
    }

    @Override
    public void render(Level l, Reactor reactor) {
        super.render(l, reactor);
        ParticleScribe.drawParticleCrucibleTop(l, ParticleTypes.SMOKE, reactor.getBlockPos(), 0.2F);
    }

    @Override
    public Status conditionsMet(Reactor reactor) {
        if (super.conditionsMet(reactor) == Status.REACTING){
            if(reactor.getTotalPowerLevel() > WorldSpecificValues.ANNIHILATION_THRESHOLD.get()){
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
