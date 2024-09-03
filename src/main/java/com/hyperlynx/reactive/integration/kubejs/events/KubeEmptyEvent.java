package com.hyperlynx.reactive.integration.kubejs.events;

import com.hyperlynx.reactive.alchemy.special.EmptyEvent;
import com.hyperlynx.reactive.integration.kubejs.KubeCrucible;

public class KubeEmptyEvent implements CrucibleKubeEvent {
    EmptyEvent event;

    public KubeEmptyEvent(EmptyEvent event){
        this.event = event;
    }

    public KubeCrucible getCrucible(){
        return new KubeCrucible(event.crucible);
    }
}
