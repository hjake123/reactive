package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.special.DissolveEvent;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.integration.kubejs.KubeWrapped;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class KubeDissolveEvent implements CrucibleKubeEvent {
    final DissolveEvent event;

    public KubeDissolveEvent(DissolveEvent event){
        this.event = event;
    }

    public ItemEntity getItemEntity(){
        return event.to_be_dissolved;
    }

    public ItemStack getItem(){
        return event.to_be_dissolved.getItem();
    }

    public KubeWrapped<CrucibleBlockEntity> getCrucible(){
        return new KubeWrapped<>(event.crucible);
    }
}
