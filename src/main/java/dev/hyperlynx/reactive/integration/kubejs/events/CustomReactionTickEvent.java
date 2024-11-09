package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.integration.kubejs.CustomReaction;
import dev.hyperlynx.reactive.integration.kubejs.KubeWrapped;
import dev.latvian.mods.kubejs.event.KubeEvent;

public class CustomReactionTickEvent implements KubeEvent, ReactorKubeEvent {
    KubeWrapped<Reactor> reactor;
    CustomReaction rxn;

    public CustomReactionTickEvent(CustomReaction rxn, Reactor reactor){
        this.reactor = new KubeWrapped<>(reactor);
        this.rxn = rxn;
    }

    @Override
    public KubeWrapped<Reactor> getReactor() {
        return reactor;
    }

    public String getAlias(){
        return rxn.getAlias();
    }
}
