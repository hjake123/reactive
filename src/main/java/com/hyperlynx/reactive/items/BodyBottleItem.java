package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BodyBottleItem extends Item{
    public BodyBottleItem(Item.Properties p) {
        super(p);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return Registration.QUARTZ_BOTTLE.get().getDefaultInstance();
    }
}