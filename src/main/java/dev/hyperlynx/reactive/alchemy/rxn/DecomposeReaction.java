package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.util.WorldSpecificValue;

import java.util.List;

public class DecomposeReaction extends Reaction{
    final List<Power> results;
    final int rate;

    public DecomposeReaction(String alias, Power reagent, Power... results) {
        super(alias, 0);
        rate = WorldSpecificValue.get(alias+"rate", 10, 20);
        reagents.put(reagent, rate);
        this.results = List.of(results);
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        reagents.forEach(reactor::expendPower);
        results.forEach((Power result) -> reactor.addPower(result, Math.min(rate/results.size(), 1)));
    }

    @Override
    public boolean isPerfect(Reactor reactor) {
        for(Power p: reactor.getPowerMap().keySet()){
            if(!reagents.containsKey(p) && !results.contains(p)){
                return false;
            }
        }
        return true;
    }
}
