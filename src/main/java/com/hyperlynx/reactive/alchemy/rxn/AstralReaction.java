package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.fx.renderers.ReactionRenders;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.management.MalformedObjectNameException;
import java.util.ArrayList;
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
        crucible.addPower(Powers.ASTRAL_POWER.get(), CrucibleBlockEntity.CRUCIBLE_MAX_POWER);
    }

    @Override
    public void render(final Level level, final CrucibleBlockEntity crucible) {
        if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) < CrucibleBlockEntity.CRUCIBLE_MAX_POWER)
            ParticleScribe.drawParticleCrucibleTop(level, Registration.STARDUST_PARTICLE.getType(), crucible.getBlockPos(), 0.3F);
    }

    @Override
    public Status conditionsMet(CrucibleBlockEntity crucible){
        if(crucible.getPowerLevel(Powers.ASTRAL_POWER.get()) > 0)
            return Status.REACTING;
        return Status.STABLE;
    }
}


