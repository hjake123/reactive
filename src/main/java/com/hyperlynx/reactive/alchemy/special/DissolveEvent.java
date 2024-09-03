package com.hyperlynx.reactive.alchemy.special;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class DissolveEvent extends Event {
    public ItemEntity to_be_dissolved;
    public CrucibleBlockEntity crucible;

    public DissolveEvent(ItemEntity to_be_dissolved, CrucibleBlockEntity crucible){
        this.to_be_dissolved = to_be_dissolved;
        this.crucible = crucible;
    }
}
