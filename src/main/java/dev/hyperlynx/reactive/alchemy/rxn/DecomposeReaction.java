package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
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
        super.run(crucible);
        reagents.forEach(crucible::expendPower);
        results.forEach((Power result) -> {
            crucible.addPower(result, Math.min(rate/results.size(), 1));
        });
    }

    @Override
    public void render(Level l, CrucibleBlockEntity crucible) {

    }

    @Override
    public boolean isPerfect(CrucibleBlockEntity crucible) {
        for(Power p: crucible.getPowerMap().keySet()){
            if(!reagents.containsKey(p) && !results.contains(p)){
                return false;
            }
        }
        return true;
    }
}
