package dev.hyperlynx.reactive.entites;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.components.ReactionFlaskContents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
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

        if (getItem().has(Registration.REACTION_FLASK_CONTENTS)) {
            contents = getItem().get(Registration.REACTION_FLASK_CONTENTS);
        } else {
            contents = new ReactionFlaskContents(generateRandomPowerCombo(), false);
        }

        ReactorEntity entity = new ReactorEntity(Registration.REACTOR_ENTITY_TYPE.get(), level());
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

    private Map<Power, Integer> generateRandomPowerCombo() {
        Map<Power, Integer> powers = new HashMap<>();
        RandomSource random = level().random;
        for(int i = 0; i < random.nextIntBetweenInclusive(1, 3); i++) {
            Power power = Powers.POWER_REGISTRY.getRandom(random).get().value();
            while(powers.equals(Powers.ASTRAL_POWER)) {
                power = Powers.POWER_REGISTRY.getRandom(random).get().value();
            }
            powers.put(power, random.nextInt(200, 500));
        }
        return powers;
    }

    @Override
    protected Item getDefaultItem() {
        return Registration.REACTION_FLASK.get();
    }
}
