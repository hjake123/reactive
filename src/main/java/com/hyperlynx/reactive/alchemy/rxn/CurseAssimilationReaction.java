package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.ParticleScribe;
import com.hyperlynx.reactive.items.CrystalIronItem;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;

public class CurseAssimilationReaction extends Reaction{
    int rate;

    public CurseAssimilationReaction(String alias){
        super(alias, 0);
        rate = WorldSpecificValues.CURSE_RATE.get();
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        crucible.expendAnyPowerExcept(Powers.CURSE_POWER.get(), rate);
        crucible.addPower(Powers.CURSE_POWER.get(), rate);

        if(Objects.requireNonNull(crucible.getLevel()).random.nextFloat() < 0.2 && crucible.getPowerLevel(Powers.CURSE_POWER.get()) >
                WorldSpecificValue.get("curse_assim_hurt_threshold", 900, 1100)){
            AABB aoe = new AABB(crucible.getBlockPos());
            aoe = aoe.inflate(3); // Inflate the AOE to be 3x the size of the crucible.
            List<LivingEntity> nearby_ents = crucible.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity e : nearby_ents) {
                if (CrystalIronItem.effectNotBlocked(crucible.getLevel(), e, 1)) {
                    e.hurt(DamageSource.MAGIC, 1);
                }
            }
        }
    }

    @Override
    public void render(final Level l, final CrucibleBlockEntity crucible) {
        ParticleScribe.drawParticleRing(l, ParticleTypes.ASH, crucible.getBlockPos(), 0.45, 0.7, 1);
    }

    @Override
    public boolean conditionsMet(CrucibleBlockEntity crucible){
        boolean has_curse = crucible.getPowerLevel(Powers.CURSE_POWER.get()) > rate;
        return crucible.getTotalPowerLevel() > (crucible.getPowerLevel(Powers.CURSE_POWER.get()) + rate) && has_curse;
    }
}


