package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.world.level.Level;

public class AstralReaction extends Reaction{
    public AstralReaction(String alias){
        super(alias, 0);
    }

    @Override
    public boolean isPerfect(CrucibleBlockEntity crucible) {
        return true;
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);
        crucible.addPower(Powers.ASTRAL_POWER.get(), CrucibleBlockEntity.CRUCIBLE_MAX_POWER);
        crucible.expendPower(Powers.CURSE_POWER.get(), WorldSpecificValues.CURSE_RATE.get() + 4);
    }

    @Override
    public void render(final Level level, final CrucibleBlockEntity crucible) {
        if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) < crucible.getTotalPowerLevel())
            ParticleScribe.drawParticleRing(level, Registration.STARDUST_PARTICLE.getType(), crucible.getBlockPos(), 0.45, 0.7, 1);
    }

    @Override
    public Status conditionsMet(CrucibleBlockEntity crucible){
        if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) > 0)
            return Status.REACTING;
        return Status.STABLE;
    }
}


