package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.integration.kubejs.KubeReactor;
import dev.latvian.mods.kubejs.event.EventJS;

public class CustomReactionTickEventJS extends EventJS implements KubeCrucibleEvent {
    KubeReactor crucible;
    String alias;

    public CustomReactionTickEventJS(String alias, Reactor crucible){
        this.crucible = new KubeReactor(crucible);
        this.alias = alias;
    }

    @Override
    public KubeReactor getCrucible() {
        return crucible;
    }

    public String getAlias(){
        return alias;
    }
}
