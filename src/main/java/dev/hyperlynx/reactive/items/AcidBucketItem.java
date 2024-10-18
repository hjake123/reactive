package dev.hyperlynx.reactive.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.level.block.Block;

public class AcidBucketItem extends SolidBucketItem {
    public AcidBucketItem(Block p_151187_, SoundEvent p_151188_, Properties p_151189_) {
        super(p_151187_, p_151188_, p_151189_);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return Items.BUCKET.getDefaultInstance();
    }
}
