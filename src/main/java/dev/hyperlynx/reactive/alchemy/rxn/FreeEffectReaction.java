package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.level.Level;

import java.util.function.Function;

// This reaction runs a particular effect function each reaction tick.
public class FreeEffectReaction extends Reaction{
    protected Function<CrucibleBlockEntity, CrucibleBlockEntity> effectFunction;
    protected Function<CrucibleBlockEntity, CrucibleBlockEntity> renderFunction;

    public FreeEffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> effect, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, int numReagents) {
        super(alias, numReagents);
        effectFunction = effect;
        renderFunction = render;
    }

    public FreeEffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power... required_powers) {
        super(alias, 0);
        effectFunction = function;
        renderFunction = render;
        for(Power required_power : required_powers)
            reagents.put(required_power, WorldSpecificValue.get(alias+required_power+"required", 1, 400));
    }

    public FreeEffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power required_power, int num_additionals) {
        super(alias, num_additionals);
        effectFunction = function;
        renderFunction = render;
        reagents.put(required_power, WorldSpecificValue.get(alias+"required", 1, 400));
    }

    @Override
    public void render(final Level l, final CrucibleBlockEntity crucible) {
        if(renderFunction != null)
            renderFunction.apply(crucible);
    }

    @Override
    public String toString() {
        return super.toString() + " - effect reaction";
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);
        if(effectFunction != null)
            effectFunction.apply(crucible);
    }
}
