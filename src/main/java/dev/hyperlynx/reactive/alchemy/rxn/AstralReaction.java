package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;

public class AstralReaction extends Reaction{
    public AstralReaction(String alias){
        super(alias, 0);
    }

    @Override
    public boolean isPerfect(Reactor crucible) {
        return true;
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        reactor.addPower(Powers.ASTRAL_POWER.get(), reactor.maxPower());
        reactor.expendPower(Powers.CURSE_POWER.get(), WorldSpecificValues.CURSE_RATE.get() + 4);
    }

    @Override
    public Status conditionsMet(Reactor reactor){
        if(reactor.getPowerLevel(Powers.ASTRAL_POWER.get()) > 0 && reactor.getTotalPowerLevel() > reactor.getPowerLevel(Powers.ASTRAL_POWER.get()))
            return Status.REACTING;
        return Status.STABLE;
    }
}


