package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.advancements.FlagCriterion;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Objects;

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
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, rate);
        }
        crucible.addPower(resultPower, rate);
        if(!Objects.requireNonNull(crucible.getLevel()).isClientSide)
            FlagCriterion.triggerForNearbyPlayers((ServerLevel) crucible.getLevel(), CriteriaTriggers.SEE_SYNTHESIS_TRIGGER, crucible.getBlockPos(), 8);
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
