package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.integration.kubejs.CustomReaction;
import dev.hyperlynx.reactive.integration.kubejs.KubeCrucible;
import dev.latvian.mods.kubejs.event.EventJS;

public class CustomReactionTickEventJS extends EventJS implements KubeCrucibleEvent {
    KubeCrucible crucible;
    String alias;

    public CustomReactionTickEventJS(String alias, CrucibleBlockEntity crucible){
        this.crucible = new KubeCrucible(crucible);
        this.alias = alias;
    }

    @Override
    public KubeCrucible getCrucible() {
        return crucible;
    }

    public String getAlias(){
        return alias;
    }
}
