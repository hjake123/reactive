package com.hyperlynx.reactive.integration.kubejs.events;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.integration.kubejs.CustomReaction;
import com.hyperlynx.reactive.integration.kubejs.KubeCrucible;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class CustomReactionTickEvent implements KubeEvent, CrucibleKubeEvent {
    KubeCrucible crucible;
    CustomReaction rxn;

    public CustomReactionTickEvent(CustomReaction rxn, CrucibleBlockEntity crucible){
        this.crucible = new KubeCrucible(crucible);
        this.rxn = rxn;
    }

    @Override
    public KubeCrucible getCrucible() {
        return null;
    }

    public String getAlias(){
        return rxn.getAlias();
    }

    public Level getLevel(){
        return crucible.crucible.getLevel();
    }

    public BlockPos getBlockPos(){
        return crucible.crucible.getBlockPos();
    }

    public void expendPower(int cost){
        for(Power p : rxn.getReagents().keySet()){
            crucible.crucible.expendPower(p, (int) ((double) cost/rxn.getReagents().size()) + 1);
            crucible.crucible.setDirty();
        }
    }
}
