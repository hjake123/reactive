package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.util.function.Function;

// This function runs a particular effect function each reaction tick.
public class EffectReaction extends Reaction{
    protected Function<CrucibleBlockEntity, CrucibleBlockEntity> effectFunction;
    protected Function<CrucibleBlockEntity, CrucibleBlockEntity> renderFunction;
    int cost;

    public EffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> effect, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, int numReagents) {
        super(alias, numReagents);
        effectFunction = effect;
        renderFunction = render;
        cost = WorldSpecificValue.get(alias+"cost", 10, 30);
    }

    public EffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power required_power) {
        super(alias, 0);
        effectFunction = function;
        renderFunction = render;
        cost = WorldSpecificValue.get(alias+"cost", 1, 50);
        reagents.put(required_power, WorldSpecificValue.get(alias+"required", 1, 400));
    }

    public EffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power required_power, int num_additionals) {
        super(alias, num_additionals);
        effectFunction = function;
        renderFunction = render;
        cost = WorldSpecificValue.get(alias+"cost", 1, 50);
        reagents.put(required_power, WorldSpecificValue.get(alias+"required", 1, 400));
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        if(effectFunction != null)
            effectFunction.apply(crucible);
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, (int) ((double) cost/reagents.size()) + 1);
            crucible.setDirty();
        }
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
}
