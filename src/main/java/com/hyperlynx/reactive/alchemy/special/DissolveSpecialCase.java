package com.hyperlynx.reactive.alchemy.special;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;

public interface DissolveSpecialCase {
    boolean attempt(CrucibleBlockEntity c, ItemEntity e);
}
