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
    int cost;

    public EffectReaction(Level l, String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, int numReagents) {
        super(l, alias, numReagents);
        effectFunction = function;
        cost = WorldSpecificValue.get(l, alias+"cost", 10, 30);
    }

    public EffectReaction(Level l, String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Power required_power) {
        super(l, alias, 0);
        effectFunction = function;
        cost = WorldSpecificValue.get(l, alias+"cost", 1, 50);
        reagents.put(required_power, WorldSpecificValue.get(l, alias+"required", 1, 400));
    }

    public EffectReaction(Level l, String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Power required_power, int num_additionals) {
        super(l, alias, num_additionals);
        effectFunction = function;
        cost = WorldSpecificValue.get(l, alias+"cost", 1, 50);
        reagents.put(required_power, WorldSpecificValue.get(l, alias+"required", 1, 400));
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
    public void render(final ClientLevel l, final CrucibleBlockEntity crucible) {
        effectFunction.apply(crucible);
    }

    @Override
    public String toString() {
        return super.toString() + " - effect reaction";
    }
}
