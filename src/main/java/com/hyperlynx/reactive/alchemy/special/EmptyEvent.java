package com.hyperlynx.reactive.alchemy.special;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class EmptyEvent extends Event {
    public CrucibleBlockEntity crucible;

    public EmptyEvent(CrucibleBlockEntity crucible){
        this.crucible = crucible;
    }

}
