package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.fx.ParticleScribe;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.function.Function;

// A reaction in which each tick the reactants destroy each other.
public class AnnihilationReaction extends EffectReaction{

    public AnnihilationReaction(String alias, Power p1, Power p2, Function<CrucibleBlockEntity, CrucibleBlockEntity> function) {
        super(alias, function, 0);
        reagents.put(p1, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
        reagents.put(p2, WorldSpecificValues.ANNIHILATION_THRESHOLD.get());
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        effectFunction.apply(crucible);
        ParticleScribe.drawParticleCrucibleTop(l, ParticleTypes.SMOKE, crucible.getBlockPos(), 0.2F);
    }

    @Override
    public boolean conditionsMet(CrucibleBlockEntity crucible) {
        return super.conditionsMet(crucible) && crucible.getTotalPowerLevel() > WorldSpecificValues.ANNIHILATION_THRESHOLD.get();
    }

    @Override
    public String toString() {
        return super.toString() + " - annihilation reaction";
    }
}
