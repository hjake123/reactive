package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Function;

// This reaction runs a particular effect function each reaction tick.
public class FreeEffectReaction extends Reaction{
    protected Consumer<Reactor> effectFunction;

    public FreeEffectReaction(String alias, Consumer<Reactor> effect, int numReagents) {
        super(alias, numReagents);
        effectFunction = effect;
    }

    public FreeEffectReaction(String alias, Consumer<Reactor> function, Power... required_powers) {
        super(alias, 0);
        effectFunction = function;
        for(Power required_power : required_powers)
            reagents.put(required_power, WorldSpecificValue.get(alias+required_power+"required", 1, 400));
    }

    public FreeEffectReaction(String alias, Consumer<Reactor> function, Power required_power, int num_additionals) {
        super(alias, num_additionals);
        effectFunction = function;
        reagents.put(required_power, WorldSpecificValue.get(alias+"required", 1, 400));
    }

    @Override
    public String toString() {
        return super.toString() + " - effect reaction";
    }

    @Override
    public void run(Reactor crucible) {
        super.run(crucible);
        if(effectFunction != null)
            effectFunction.accept(crucible);
    }
}
