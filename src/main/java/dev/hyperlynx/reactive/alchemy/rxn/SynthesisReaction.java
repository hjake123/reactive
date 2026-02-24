package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.util.WorldSpecificValue;

public class SynthesisReaction extends Reaction{
    final Power resultPower;
    final int rate;

    public SynthesisReaction(String alias, Power resultPower, Power... reagents) {
        super(alias, reagents);
        // Adjust the power requirements for Synthesis reaction to make them not prohibitively expensive.
        // See issue #128
        this.reagents.replaceAll((p, v) -> Math.max(1, this.reagents.get(p) / 2));
        rate = WorldSpecificValue.get(alias+"rate", 40, 100);
        this.resultPower = resultPower;
    }

    @Override
    public void run(Reactor crucible) {
        super.run(crucible);
        int expended = 0;
        for(Power p : reagents.keySet()){
            expended += crucible.getPowerLevel(p);
            crucible.expendPower(p, rate);
        }
        int effective_rate = Math.min(rate, expended);
        crucible.addPower(resultPower, effective_rate);
    }

    @Override
    public boolean isPerfect(Reactor crucible) {
        for(Power p: crucible.getPowerMap().keySet()){
            if(!reagents.containsKey(p) && !p.equals(resultPower)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " - synthesis reaction";
    }
}
