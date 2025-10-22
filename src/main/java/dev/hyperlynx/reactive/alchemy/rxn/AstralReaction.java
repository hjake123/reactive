package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;

public class AstralReaction extends Reaction{
    public AstralReaction(String alias){
        super(alias, 0);
    }

    @Override
    public boolean isPerfect(Reactor crucible) {
        return true;
    }

    @Override
    public void run(Reactor crucible) {
        super.run(crucible);
        crucible.addPower(Powers.ASTRAL_POWER.get(), CrucibleBlockEntity.CRUCIBLE_MAX_POWER);
        crucible.expendPower(Powers.CURSE_POWER.get(), WorldSpecificValues.CURSE_RATE.get());
    }

    @Override
    public Status conditionsMet(Reactor crucible){
        if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) > 0)
            return Status.REACTING;
        return Status.STABLE;
    }
}


