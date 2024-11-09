package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.util.WorldSpecificValue;

import java.util.function.Consumer;

// This reaction runs a particular effect function each reaction tick and removes power according to the cost when it does
public class EffectReaction extends FreeEffectReaction{
    int cost;

    public EffectReaction(String alias, Consumer<Reactor> effect, Consumer<Reactor> render, int numReagents) {
        super(alias, effect, render, numReagents);
        cost = WorldSpecificValue.get(alias+"cost", 10, 20);
    }

    public EffectReaction(String alias, Consumer<Reactor> function, Consumer<Reactor> render, Power required_power) {
        super(alias, function, render, required_power);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    public EffectReaction(String alias, Consumer<Reactor> function, Consumer<Reactor> render, Power... required_powers) {
        super(alias, function, render, required_powers);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    public EffectReaction(String alias, Consumer<Reactor> function, Consumer<Reactor> render, Power required_power, int num_additionals) {
        super(alias, function, render, required_power, num_additionals);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        for(Power p : reagents.keySet()){
            reactor.expendPower(p, (int) ((double) cost/reagents.size()) + 1);
            reactor.setDirty();
        }
    }
}
