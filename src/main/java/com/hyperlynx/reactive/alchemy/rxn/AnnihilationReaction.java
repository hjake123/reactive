package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.function.Function;

// A reaction in which each tick the reactants destroy each other.
public class AnnihilationReaction extends EffectReaction{

    public AnnihilationReaction(Level l, String alias, Power p1, Power p2, Function<CrucibleBlockEntity, CrucibleBlockEntity> function) {
        super(l, alias, function, 0);
        reagents.put(p1, WorldSpecificValues.ANNIHILATION_THRESHOLD.get(l));
        reagents.put(p2, WorldSpecificValues.ANNIHILATION_THRESHOLD.get(l));
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        effectFunction.apply(crucible);
        Helper.drawParticleCrucibleTop(l, ParticleTypes.SMOKE, crucible.getBlockPos(), 0.2F);
    }

    @Override
    public boolean conditionsMet(CrucibleBlockEntity crucible) {
        return super.conditionsMet(crucible) && crucible.getPowerLevel(Powers.BODY_POWER.get()) < WorldSpecificValues.ANNIHILATION_THRESHOLD.get(crucible.getLevel());
    }

    @Override
    public String toString() {
        return super.toString() + " - annihilation reaction";
    }
}
