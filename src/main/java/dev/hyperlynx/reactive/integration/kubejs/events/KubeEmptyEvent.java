package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.special.EmptyEvent;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.integration.kubejs.KubeWrapped;

public class KubeEmptyEvent implements CrucibleKubeEvent {
    EmptyEvent event;

    public KubeEmptyEvent(EmptyEvent event){
        this.event = event;
    }

    public KubeWrapped<CrucibleBlockEntity> getCrucible(){
        return new KubeWrapped<>(event.crucible);
    }
}
