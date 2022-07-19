package com.hyperlynx.reactive.alchemy.rxn;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.Helper;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssimilationReaction extends Reaction{
    Power consumedPower;
    Power producedPower;
    int rate;

    public AssimilationReaction(Level l, String alias) {
        super(l, alias, 2);
        ArrayList<Power> reagents_copy = new ArrayList<>(reagents.keySet());
        consumedPower = reagents_copy.get(0);
        producedPower = reagents_copy.get(1);
        rate = WorldSpecificValue.get(l, alias+"rate", 10, 20);
        reagents.replace(consumedPower, rate);
    }

    public AssimilationReaction(Level l, String alias, Power producedPower){
        super(l, alias, 1);
        consumedPower = reagents.keySet().stream().findFirst().get();
        reagents.put(producedPower, WorldSpecificValue.get(l, alias+"r"+1, 1, 100));
        this.producedPower = producedPower;
        rate = WorldSpecificValue.get(l, alias+"rate", 10, 20);
        reagents.replace(consumedPower, rate);
    }

    @Override
    public void run(CrucibleBlockEntity crucible) {
        crucible.expendPower(consumedPower, rate);
        crucible.addPower(producedPower, rate);
    }

    @Override
    public void render(ClientLevel l, CrucibleBlockEntity crucible) {
        //Helper.drawParticlesCrucibleTop(l, ParticleTypes.ENCHANTED_HIT, crucible.getBlockPos(), 0.2F);
    }
}
