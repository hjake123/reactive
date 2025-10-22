package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.util.WorldSpecificValue;

import java.util.function.Consumer;
import java.util.function.Function;

// This reaction runs a particular effect function each reaction tick and removes power according to the cost when it does
public class EffectReaction extends FreeEffectReaction{
    int cost;

    public EffectReaction(String alias, Consumer<Reactor> effect, int numReagents) {
        super(alias, effect, numReagents);
        cost = WorldSpecificValue.get(alias+"cost", 10, 20);
    }

    public EffectReaction(String alias, Consumer<Reactor> function, Power required_power) {
        super(alias, function, required_power);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    public EffectReaction(String alias, Consumer<Reactor> function, Power... required_powers) {
        super(alias, function, required_powers);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    @Override
    public void run(Reactor crucible) {
        super.run(crucible);
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, (int) ((double) cost/reagents.size()) + 1);
            crucible.setDirty();
        }
    }
}
