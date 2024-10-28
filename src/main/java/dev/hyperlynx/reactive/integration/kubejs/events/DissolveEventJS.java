package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.special.DissolveEvent;
import dev.hyperlynx.reactive.integration.kubejs.KubeCrucible;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class DissolveEventJS extends EventJS implements KubeCrucibleEvent {
    DissolveEvent event;

    public DissolveEventJS(DissolveEvent event){
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
