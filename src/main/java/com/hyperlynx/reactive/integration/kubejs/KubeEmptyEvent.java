package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.special.EmptyEvent;

public class KubeEmptyEvent implements CrucibleKubeEvent {
    EmptyEvent event;

    public KubeEmptyEvent(EmptyEvent event){
        this.event = event;
    }

    public KubeCrucible getCrucible(){
        return new KubeCrucible(event.crucible);
    }
}
