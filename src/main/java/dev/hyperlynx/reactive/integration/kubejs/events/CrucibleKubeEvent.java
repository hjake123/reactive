package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.integration.kubejs.KubeWrapped;

public interface CrucibleKubeEvent extends ReactorKubeEvent {
    abstract KubeWrapped<CrucibleBlockEntity> getCrucible();

    default KubeWrapped<Reactor> getReactor(){
        return new KubeWrapped<>(getCrucible().get());
    }
}
