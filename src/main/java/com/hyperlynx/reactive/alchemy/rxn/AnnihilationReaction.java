package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

// A reaction in which each tick the reactants destroy each other.
public class AnnihilationReaction extends Reaction{
    int rate;

    public AnnihilationReaction(Level l, String alias) {
        super(l, alias, 3);
        rate = WorldSpecificValue.get(l, alias+"rate", 1, 10);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        for(Power p : reagents.keySet()){
            crucible.expendPower(p, rate);
            crucible.setDirty();
        }
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        Helper.drawParticlesCrucibleTop(l, ParticleTypes.SMOKE, crucible.getBlockPos(), rate);
    }
}
