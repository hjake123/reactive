package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.special.DissolveEvent;
import dev.hyperlynx.reactive.integration.kubejs.KubeCrucible;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class DissoleEventJS extends EventJS implements KubeCrucibleEvent {
    DissolveEvent event;

    public DissoleEventJS(DissolveEvent event){
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
