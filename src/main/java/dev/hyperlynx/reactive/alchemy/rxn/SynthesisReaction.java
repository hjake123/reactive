package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.advancements.CriteriaTriggers;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.advancements.FlagCriterion;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

public class SynthesisReaction extends Reaction{
    Power resultPower;
    int rate;

    public SynthesisReaction(String alias, Power resultPower, Power... reagents) {
        super(alias, reagents);
        // Adjust the power requirements for Synthesis reaction to make them not prohibitively expensive.
        // See issue #128
        this.reagents.replaceAll((p, v) -> Math.max(1, this.reagents.get(p) / 2));
        rate = WorldSpecificValue.get(alias+"rate", 40, 100);
        this.resultPower = resultPower;
    }

    @Override
    public void run(Reactor crucible) {
        super.run(crucible);
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, rate);
        }
        crucible.addPower(resultPower, rate);
        if(!Objects.requireNonNull(crucible.obtainLevel()).isClientSide)
            FlagCriterion.triggerForNearbyPlayers((ServerLevel) crucible.obtainLevel(), CriteriaTriggers.SEE_SYNTHESIS_TRIGGER, crucible.blockPos(), 8);
    }

    @Override
    public boolean isPerfect(Reactor crucible) {
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
