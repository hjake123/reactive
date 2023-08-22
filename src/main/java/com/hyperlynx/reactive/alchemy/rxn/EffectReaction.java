package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Function;

// This reaction runs a particular effect function each reaction tick and removes power according to the cost when it does
public class EffectReaction extends FreeEffectReaction{
    int cost;

    public EffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> effect, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, int numReagents) {
        super(alias, effect, render, numReagents);
        cost = WorldSpecificValue.get(alias+"cost", 10, 20);
    }

    public EffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power required_power) {
        super(alias, function, render, required_power);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    public EffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power... required_powers) {
        super(alias, function, render, required_powers);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    public EffectReaction(String alias, Function<CrucibleBlockEntity, CrucibleBlockEntity> function, Function<CrucibleBlockEntity, CrucibleBlockEntity> render, Power required_power, int num_additionals) {
        super(alias, function, render, required_power, num_additionals);
        cost = WorldSpecificValue.get(alias+"cost", 1, 20);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, (int) ((double) cost/reagents.size()) + 1);
            crucible.setDirty();
        }
    }
}
