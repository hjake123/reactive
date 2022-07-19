package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

public class CurseAssimilationReaction extends Reaction{
    int rate;

    public CurseAssimilationReaction(Level l, String alias){
        super(l, alias, 0);
        rate = WorldSpecificValue.get(l, alias+"rate", 10, 20);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        crucible.expendAnyPowerExcept(Registration.CURSE_POWER.get(), rate);
        crucible.addPower(Registration.CURSE_POWER.get(), rate);
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        Helper.drawParticleRing(l, ParticleTypes.ASH, crucible.getBlockPos(), 0.45, 0.7, 1);
    }

    @Override
    public boolean conditionsMet(CrucibleBlockEntity crucible){
        boolean has_curse = crucible.getPowerLevel(Registration.CURSE_POWER.get()) > rate;
        return crucible.getTotalPowerLevel() > (crucible.getPowerLevel(Registration.CURSE_POWER.get()) + rate) && has_curse;
    }
}


