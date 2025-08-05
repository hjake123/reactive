package dev.hyperlynx.reactive.entites;

import dev.hyperlynx.reactive.registration.*;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.components.ReactionFlaskContents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.Map;

public class ThrownReactionFlask extends ThrowableItemProjectile {
    public ThrownReactionFlask(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        level().playSound(null, result.getLocation().x, result.getLocation().y, result.getLocation().z, SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS);
        ReactionFlaskContents contents;

        if (getItem().has(ReactiveComponentTypes.REACTION_FLASK_CONTENTS)) {
            contents = getItem().get(ReactiveComponentTypes.REACTION_FLASK_CONTENTS);
        } else {
            contents = new ReactionFlaskContents(Power.generateRandomPowerCombo(level()), false);
        }
        if(contents == null) {
            return;
        }

        ReactorEntity entity = new ReactorEntity(ReactiveEntityTypes.REACTOR.get(), level());
        entity.setPos(result.getLocation().add(0, 1.0, 0));
        Map<Power, Integer> powers = contents.powers();
        entity.setLifespan(600);
        entity.setPowers(powers);
        entity.setElectricCharge(contents.electric_charge() ? 100 : 0);
        level().addFreshEntity(entity);

        for(Power power : powers.keySet()){
            ParticleScribe.drawExactParticleRing(level(), new EnergyParticle.Options(0.1F, power.getColor(), entity.position()),
                    result.getLocation(), 0.2, 5);
        }
        this.kill();
    }

    @Override
    protected Item getDefaultItem() {
        return ReactiveItems.REACTION_FLASK.get();
    }
}
