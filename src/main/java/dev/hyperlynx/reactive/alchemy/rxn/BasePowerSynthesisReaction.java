package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.advancements.FlagTrigger;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

public class BasePowerSynthesisReaction extends SynthesisReaction {
    public BasePowerSynthesisReaction(String alias, Power resultPower, Power... reagents) {
        super(alias, resultPower, reagents);
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        if (!Objects.requireNonNull(reactor.getLevel()).isClientSide)
            FlagTrigger.triggerForNearbyPlayers((ServerLevel) reactor.getLevel(), Registration.SEE_SYNTHESIS_TRIGGER.get(), reactor.getBlockPos(), 8);
    }
}
