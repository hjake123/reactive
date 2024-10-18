package dev.hyperlynx.reactive.alchemy.special;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.neoforged.bus.api.Event;

public class EmptyEvent extends Event {
    public CrucibleBlockEntity crucible;

    public EmptyEvent(CrucibleBlockEntity crucible){
        this.crucible = crucible;
    }

}
