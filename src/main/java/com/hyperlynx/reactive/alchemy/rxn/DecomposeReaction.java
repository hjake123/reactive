package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.util.List;

public class DecomposeReaction extends Reaction{
    List<Power> results;
    int rate;

    public DecomposeReaction(String alias, Power reagent, Power... results) {
        super(alias, 0);
        rate = WorldSpecificValue.get(alias+"rate", 10, 20);
        reagents.put(reagent, rate);
        this.results = List.of(results);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        reagents.forEach(crucible::expendPower);
        results.forEach((Power result) -> {
            crucible.addPower(result, Math.min(rate/results.size(), 1));
        });
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {

    }
}
