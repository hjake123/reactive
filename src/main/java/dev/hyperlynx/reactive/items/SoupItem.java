package dev.hyperlynx.reactive.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SoupItem extends Item {
    public SoupItem(Properties p_40682_) {
        super(p_40682_);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level l, LivingEntity eater) {
        ItemStack itemstack = super.finishUsingItem(stack, l, eater);
        if(eater instanceof Player && !((Player)eater).isCreative()){
            ((Player)eater).addItem(new ItemStack(Items.BOWL));
        }
        return itemstack;
    }
}
