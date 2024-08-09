package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.advancements.FlagTrigger;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

public class BasePowerSynthesisReaction extends SynthesisReaction {
    public BasePowerSynthesisReaction(String alias, Power resultPower, Power... reagents) {
        super(alias, resultPower, reagents);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);
        if (!Objects.requireNonNull(crucible.getLevel()).isClientSide)
            FlagTrigger.triggerForNearbyPlayers((ServerLevel) crucible.getLevel(), CriteriaTriggers.SEE_SYNTHESIS_TRIGGER.get(), crucible.getBlockPos(), 8);
    }
}
