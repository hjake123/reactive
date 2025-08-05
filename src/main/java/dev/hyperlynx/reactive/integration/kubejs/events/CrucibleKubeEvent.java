package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.integration.kubejs.KubeWrapped;

@SuppressWarnings("unused")
public interface CrucibleKubeEvent extends ReactorKubeEvent {
    KubeWrapped<CrucibleBlockEntity> getCrucible();

    default KubeWrapped<Reactor> getReactor(){
        return new KubeWrapped<>(getCrucible().get());
    }
}
