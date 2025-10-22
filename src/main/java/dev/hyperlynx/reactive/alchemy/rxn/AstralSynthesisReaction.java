package dev.hyperlynx.reactive.alchemy.rxn;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Objects;

public class AstralSynthesisReaction extends SynthesisReaction{
    public AstralSynthesisReaction(String alias, Power resultPower, Power... reagents) {
        super(alias, resultPower, reagents);
        this.reagents.replaceAll((p, v) -> 1);
    }

    @Override
    public void run(Reactor reactor) {
        super.run(reactor);
        reactor.addPower(Powers.ASTRAL_POWER.get(), CrucibleBlockEntity.CRUCIBLE_MAX_POWER);
        Objects.requireNonNull(reactor.getLevel()).playSound(null, reactor.getBlockPos(), SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 0.5F, 1.2F);
        ParticleScribe.drawParticleSphere(Objects.requireNonNull(reactor.getLevel()), Registration.STARDUST_PARTICLE, reactor.getBlockPos(), 0.5, 1.0, 20);
        Objects.requireNonNull(reactor.getLevel()).playSound(null, reactor.getBlockPos(), Registration.RUMBLE_SOUND.get(), SoundSource.BLOCKS);
        if(reactor instanceof CrucibleBlockEntity crucible){
            crucible.integrity = 12;
        }
    }
}
