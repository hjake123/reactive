package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class BlazeBurnReaction extends Reaction{
    int threshold = 0;

    public BlazeBurnReaction(Level l, String alias) {
        super(new HashMap<>());
        threshold = WorldSpecificValue.get(l, alias+"_threshold", 50, 100);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        crucible.expendPower(Powers.BLAZE_POWER.get(), 30);
        crucible.setDirty();
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        Helper.drawParticleCrucibleTop(l, ParticleTypes.SMOKE, crucible.getBlockPos(), 0.4F);
    }

    @Override
    public boolean conditionsMet(CrucibleBlockEntity crucible){
        if(threshold > crucible.getPowerLevel(Powers.BLAZE_POWER.get())){
            return false;
        }
        return crucible.getPowerLevel(Powers.BLAZE_POWER.get()) < crucible.getPowerLevel(Powers.ACID_POWER.get());
    }
}
