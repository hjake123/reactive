package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.items.CrystalIronItem;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public class CurseAssimilationReaction extends Reaction{
    int rate;

    public CurseAssimilationReaction(String alias){
        super(alias, 0);
        rate = WorldSpecificValues.CURSE_RATE.get();
    }

    @Override
    public boolean isPerfect(Reactor crucible) {
        return true;
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        reactor.expendAnyPowerExcept(Powers.ASTRAL_POWER.get(reactor), rate);
        reactor.addPower(Powers.CURSE_POWER.get(reactor), rate);

        if(Objects.requireNonNull(reactor.getLevel()).random.nextFloat() < 0.2 && reactor.getPowerLevel(Powers.CURSE_POWER.get(reactor)) >
                WorldSpecificValue.get("curse_assim_hurt_threshold", 900, 1100)){
            AABB aoe = new AABB(reactor.getBlockPos());
            aoe = aoe.inflate(3); // Inflate the AOE to be 3x the size of the reactor.
            List<LivingEntity> nearby_ents = reactor.getLevel().getEntitiesOfClass(LivingEntity.class, aoe);
            for (LivingEntity e : nearby_ents) {
                if (CrystalIronItem.effectNotBlocked(e, 1)) {
                    e.hurt(reactor.getLevel().damageSources().magic(), 1);
                }
            }
        }
    }

    @Override
    public void render(final Level l, final Reactor reactor) {
        ParticleScribe.drawParticleRing(l, ParticleTypes.ASH, reactor.getBlockPos(), 0.45, 0.7, 1);
    }

    @Override
    public Status conditionsMet(Reactor reactor){
        boolean has_curse = reactor.getPowerLevel(Powers.CURSE_POWER.get(reactor)) > rate;
        if (reactor.getTotalPowerLevel() > (reactor.getPowerLevel(Powers.CURSE_POWER.get(reactor)) + rate) && has_curse)
            return Status.REACTING;
        return Status.STABLE;
    }
}


