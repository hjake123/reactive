package com.hyperlynx.reactive.alchemy.special;

import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.item.ItemStack;

public interface BottleSpecialCase {
    ItemStack attempt(CrucibleBlockEntity c, ItemStack b);
}
