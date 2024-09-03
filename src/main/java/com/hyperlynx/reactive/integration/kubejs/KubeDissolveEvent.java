package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.special.DissolveEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class KubeDissolveEvent implements CrucibleKubeEvent {
    DissolveEvent event;

    public KubeDissolveEvent(DissolveEvent event){
        this.event = event;
    }

    public ItemEntity getItemEntity(){
        return event.to_be_dissolved;
    }

    public ItemStack getItem(){
        return event.to_be_dissolved.getItem();
    }

    public KubeCrucible getCrucible(){
        return new KubeCrucible(event.crucible);
    }
}
