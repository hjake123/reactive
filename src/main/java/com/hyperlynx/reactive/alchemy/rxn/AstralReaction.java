package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.client.particles.ParticleScribe;
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


