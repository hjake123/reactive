package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

// This reaction runs a particular effect function each reaction tick.
public class FreeEffectReaction extends Reaction{
    protected Consumer<Reactor> effectFunction;
    protected Consumer<Reactor> renderFunction;

    public FreeEffectReaction(String alias, Consumer<Reactor> effect, Consumer<Reactor> render, int numReagents) {
        super(alias, numReagents);
        effectFunction = effect;
        renderFunction = render;
    }

    public FreeEffectReaction(String alias, Consumer<Reactor> function, Consumer<Reactor> render, Power... required_powers) {
        super(alias, 0);
        effectFunction = function;
        renderFunction = render;
        for(Power required_power : required_powers)
            reagents.put(required_power, WorldSpecificValue.get(alias+required_power+"required", 1, 400));
    }

    public FreeEffectReaction(String alias, Consumer<Reactor> function, Consumer<Reactor> render, Power required_power, int num_additionals) {
        super(alias, num_additionals);
        effectFunction = function;
        renderFunction = render;
        reagents.put(required_power, WorldSpecificValue.get(alias+"required", 1, 400));
    }

    @Override
    public void render(final Level l, final Reactor reactor) {
        if(renderFunction != null)
            renderFunction.accept(reactor);
    }

    @Override
    public String toString() {
        return super.toString() + " - effect reaction";
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        if(effectFunction != null)
            effectFunction.accept(reactor);
    }
}
