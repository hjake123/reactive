package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

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
        crucible.expendAnyPowerExcept(Powers.ASTRAL_POWER.get(), CrucibleBlockEntity.CRUCIBLE_MAX_POWER);
        crucible.expendPower(Powers.CURSE_POWER.get(), WorldSpecificValues.CURSE_RATE.get() + 30);
        crucible.addPower(Powers.ASTRAL_POWER.get(), CrucibleBlockEntity.CRUCIBLE_MAX_POWER);
    }

    @Override
    public void render(final Level l, final CrucibleBlockEntity crucible) {
        if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) < crucible.getTotalPowerLevel())
            ParticleScribe.drawParticleCrucibleTop(l, Registration.STARDUST_PARTICLE.getType(), crucible.getBlockPos(), 0.3F);
    }

    @Override
    public Status conditionsMet(CrucibleBlockEntity crucible){
        if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) > 0)
            return Status.REACTING;
        return Status.STABLE;
    }
}


