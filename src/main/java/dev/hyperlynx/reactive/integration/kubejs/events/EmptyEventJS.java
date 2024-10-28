package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.special.EmptyEvent;
import dev.hyperlynx.reactive.integration.kubejs.KubeCrucible;
import dev.latvian.mods.kubejs.event.EventJS;

public class EmptyEventJS extends EventJS implements KubeCrucibleEvent {
    EmptyEvent event;

    public EmptyEventJS(EmptyEvent event){
        this.event = event;
    }

    public KubeCrucible getCrucible(){
        return new KubeCrucible(event.crucible);
    }
}
