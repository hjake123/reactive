package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.level.Level;

public class SynthesisReaction extends Reaction{
    Power resultPower;
    int rate;

    public SynthesisReaction(String alias, Power resultPower, Power... reagents) {
        super(alias, reagents);
        rate = WorldSpecificValue.get(alias+"rate", 40, 100);
        this.resultPower = resultPower;
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);
        int expended = 0;
        for(Power p : reagents.keySet()){
            expended += crucible.getPowerLevel(p);
            crucible.expendPower(p, rate);
        }
        int effective_rate = Math.min(rate, expended);
        crucible.addPower(resultPower, effective_rate);
    }

    @Override
    public void render(Level l, CrucibleBlockEntity crucible) {

    }

    @Override
    public boolean isPerfect(CrucibleBlockEntity crucible) {
        for(Power p: crucible.getPowerMap().keySet()){
            if(!reagents.containsKey(p) && !p.equals(resultPower)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " - synthesis reaction";
    }
}
