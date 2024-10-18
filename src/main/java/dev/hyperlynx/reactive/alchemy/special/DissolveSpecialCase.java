package dev.hyperlynx.reactive.alchemy.special;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;

public interface DissolveSpecialCase {
    boolean attempt(CrucibleBlockEntity c, ItemEntity e);
}
