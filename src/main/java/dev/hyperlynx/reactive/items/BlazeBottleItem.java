package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlazeBottleItem extends PowerBottleItem {
    public BlazeBottleItem(Properties props, Block block) {
        super(props, block);
    }

    int tick_count = 0;

    // Blaze bottles are hot; holding one hurts you.
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot_number, boolean unknown) {
        super.inventoryTick(stack, level, entity, slot_number, unknown);
        if(!level.isClientSide){
            if(entity instanceof Player && !((Player) entity).isCreative() && ((Player) entity).isHolding(Registration.BLAZE_BOTTLE.get())){
                tick_count++;
                if(tick_count > 20){
                    entity.hurt(level.damageSources().inFire(), 2);
                    tick_count = 0;
                }
            }else{
                tick_count = 0;
            }
        }
    }

    @Override
    public @NotNull ItemStack getCraftingRemainder(ItemStack stack) {
        return ItemStack.EMPTY;
    }
}
