package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class OmenConversionReaction extends Reaction {
    public OmenConversionReaction(String alias) {
        super(alias, Powers.BLAZE_POWER.get());
        reagents.put(Powers.OMEN_POWER.get(), WorldSpecificValue.get("omen_balance_for_conversion", 100, 150));
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        super.run(crucible);

        crucible.expendPower(Powers.BLAZE_POWER.get(), WorldSpecificValue.get("omen_conversion_cost", 20, 40));
        int omen = crucible.getPowerLevel(Powers.OMEN_POWER.get());
        crucible.expendPower(Powers.OMEN_POWER.get(), omen);
        crucible.addPower(Powers.SOUL_POWER.get(), omen / 2);

        Level level = Objects.requireNonNull(crucible.getLevel());
        level.playSound(null, crucible.getBlockPos(), SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.1F);
        ParticleScribe.drawParticleSphere(level, ParticleTypes.SOUL_FIRE_FLAME, crucible.getBlockPos(), 0.7, 0.5, 10);
        crucible.setDirty();
    }

    @Override
    public void render(Level l, CrucibleBlockEntity crucible) {

    }


}