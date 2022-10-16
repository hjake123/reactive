package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PowerBottleItem extends Item {
    public PowerBottleItem(Properties props) {
        super(props);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return Registration.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

}
