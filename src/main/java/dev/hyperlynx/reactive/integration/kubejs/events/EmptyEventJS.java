package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.special.EmptyEvent;
import dev.hyperlynx.reactive.integration.kubejs.KubeReactor;
import dev.latvian.mods.kubejs.event.EventJS;

public class EmptyEventJS extends EventJS implements KubeCrucibleEvent {
    EmptyEvent event;

    public EmptyEventJS(EmptyEvent event){
        this.event = event;
    }

    public KubeReactor getCrucible(){
        return new KubeReactor(event.crucible);
    }
}
