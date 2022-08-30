package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.util.function.Function;

// This function runs a particular effect function each reaction tick.
public class EffectReaction extends Reaction{
    protected Function<CrucibleBlockEntity, CrucibleBlockEntity> effectFunction;
    int cost;

    public EffectReaction(Level l, String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function) {
        super(l, alias, 4);
        effectFunction = function;
        cost = WorldSpecificValue.get(l, alias+"cost", 1, 50);
        System.out.println(reagents + " - " + effectFunction.toString());
        if(WorldSpecificValues.ELECTRIC_EFFECT.get(l) == 3){
            stimulus = ReactionStimuli.ELECTRIC;
        }
    }

    public EffectReaction(Level l, String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Power required_power) {
        super(l, alias, 3);
        effectFunction = function;
        cost = WorldSpecificValue.get(l, alias+"cost", 1, 50);
        reagents.put(required_power, WorldSpecificValue.get(l, alias+"required", 1, 400));
        System.out.println(reagents + " - " + effectFunction.toString());
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        effectFunction.apply(crucible);
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, cost/reagents.size());
            crucible.setDirty();
        }
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        effectFunction.apply(crucible);
    }
}
