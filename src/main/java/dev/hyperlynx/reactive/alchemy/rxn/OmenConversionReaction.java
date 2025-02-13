package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class OmenConversionReaction extends Reaction {
    public OmenConversionReaction(String alias, RegistryAccess access) {
        super(alias, Powers.BLAZE_POWER.get(access));
        reagents.put(Powers.OMEN_POWER.get(access), WorldSpecificValue.get("omen_balance_for_conversion", 100, 150));
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);

        reactor.expendPower(Powers.BLAZE_POWER.get(reactor), WorldSpecificValue.get("omen_conversion_cost", 20, 40));
        int omen = reactor.getPowerLevel(Powers.OMEN_POWER.get(reactor));
        reactor.expendPower(Powers.OMEN_POWER.get(reactor), omen);
        reactor.addPower(Powers.SOUL_POWER.get(reactor), omen / 2);

        Level level = Objects.requireNonNull(reactor.getLevel());
        level.playSound(null, reactor.getBlockPos(), SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.1F);
        ParticleScribe.drawParticleSphere(level, ParticleTypes.SOUL_FIRE_FLAME, reactor.getBlockPos(), 0.7, 0.5, 10);
        reactor.setDirty();
    }

    @Override
    public void render(Level l, Reactor crucible) {

    }


}