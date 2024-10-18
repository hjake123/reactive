package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.special.EmptyEvent;
import dev.hyperlynx.reactive.integration.kubejs.KubeCrucible;

public class KubeEmptyEvent implements CrucibleKubeEvent {
    EmptyEvent event;

    public KubeEmptyEvent(EmptyEvent event){
        this.event = event;
    }

    public KubeCrucible getCrucible(){
        return new KubeCrucible(event.crucible);
    }
}
