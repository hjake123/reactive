package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;

import java.util.function.Consumer;
import java.util.function.Function;

// A reaction in which each tick the reactants destroy each other.
public class AnnihilationReaction extends EffectReaction{

    public AnnihilationReaction(String alias, Power p1, Power p2, Consumer<Reactor> function) {
        super(alias, function,0);
        reagents.put(p1, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
        reagents.put(p2, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
    }

    @Override
    public Status conditionsMet(Reactor crucible) {
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
