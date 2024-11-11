package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.world.level.Level;

public class AstralReaction extends Reaction{
    public AstralReaction(String alias){
        super(alias, 0);
    }

    @Override
    public boolean isPerfect(Reactor crucible) {
        return true;
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        reactor.addPower(Powers.ASTRAL_POWER.get(), reactor.maxPower());
        reactor.expendPower(Powers.CURSE_POWER.get(), WorldSpecificValues.CURSE_RATE.get() + 4);
    }

    @Override
    public void render(final Level level, final Reactor reactor) {
        if(reactor.getPowerLevel(Powers.ASTRAL_POWER.get()) < reactor.getTotalPowerLevel())
            ParticleScribe.drawParticleRing(level, Registration.STARDUST_PARTICLE.getType(), reactor.getBlockPos(), 0.45, 0.7, 1);
    }

    @Override
    public Status conditionsMet(Reactor reactor){
        if(reactor.getPowerLevel(Powers.ASTRAL_POWER.get()) > 0)
            return Status.REACTING;
        return Status.STABLE;
    }
}


