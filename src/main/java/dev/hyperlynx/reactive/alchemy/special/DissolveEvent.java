package dev.hyperlynx.reactive.alchemy.special;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.Event;

public class DissolveEvent extends Event {
    public final ItemEntity to_be_dissolved;
    public final CrucibleBlockEntity crucible;

    public DissolveEvent(ItemEntity to_be_dissolved, CrucibleBlockEntity crucible){
        this.to_be_dissolved = to_be_dissolved;
        this.crucible = crucible;
    }
}
