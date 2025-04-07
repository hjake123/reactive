package dev.hyperlynx.reactive.entites;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
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
        if(!getItem().has(Registration.REACTOR_DATA)){
            this.kill();
            return;
        }

        ReactorEntity entity = new ReactorEntity(Registration.REACTOR_ENTITY_TYPE.get(), level());
        entity.setPos(result.getLocation().add(0, 1.0, 0));
        Map<Power, Integer> powers = getItem().get(Registration.REACTOR_DATA).powers();
        entity.setLifespan(600);
        entity.setPowers(powers);
        level().addFreshEntity(entity);

        for(Power power : powers.keySet()){
            ParticleScribe.drawExactParticleRing(level(), new EnergyParticle.Options(0.1F, power.getColor(), entity.position()),
                    result.getLocation(), 0.2, 5);
        }
        this.kill();
    }

    @Override
    protected Item getDefaultItem() {
        return Registration.REACTION_FLASK.get();
    }
}
