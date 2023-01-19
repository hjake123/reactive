package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.FlagCriterion;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
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
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, (int) Math.ceil(rate/(double) reagents.size()));
        }
        crucible.addPower(resultPower, rate);
        if(!Objects.requireNonNull(crucible.getLevel()).isClientSide)
            FlagCriterion.triggerForNearbyPlayers((ServerLevel) crucible.getLevel(), Registration.SEE_SYNTHESIS_TRIGGER, crucible.getBlockPos(), 8);
    }

    @Override
    public void render(final ClientLevel l, final CrucibleBlockEntity crucible) {
        // No need.
    }

    @Override
    public String toString() {
        return super.toString() + " - synthesis reaction";
    }
}
