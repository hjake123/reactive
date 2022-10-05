package com.hyperlynx.reactive.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BlazeBottleItem extends Item {
    public BlazeBottleItem(Properties p) {
        super(p);
    }

    int tick_count = 0;

    // Blaze bottles are hot to the touch
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int tick, boolean unknown) {
        super.inventoryTick(stack, level, entity, tick, unknown);
        if(!level.isClientSide){
            if(entity instanceof Player && !((Player) entity).isCreative()){
                tick_count++;
                if(((Player) entity).getMainHandItem().is(this) && tick_count > 20){
                    entity.hurt(DamageSource.IN_FIRE, 2);
                    tick_count = 0;
                }
            }else{
                tick_count = 0;
            }
        }
    }
}
