package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LightBottleItem extends Item{
    public LightBottleItem(Item.Properties p) {
        super(p);
    }

//    int tick_count = 0;

//    // Light bottles will empty themselves if left in the inventory too long.
//    @Override
//    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean unknown) {
//        super.inventoryTick(stack, level, entity, slot, unknown);
//        if(!level.isClientSide){
//            if(entity instanceof Player && !((Player) entity).isCreative()) {
//                tick_count++;
//                if (tick_count > 50) {
//                    ((Player) entity).getInventory().setItem(slot, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
//                    tick_count = 0;
//                }
//            }
//        }
//    }
}