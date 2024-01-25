package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShulkerCrucibleItem extends BlockItem {
    public static final String TAG_LABEL = "Label";
    public ShulkerCrucibleItem(Properties props) {
        super(Registration.SHULKER_CRUCIBLE.get(), props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tf) {
        if(stack.hasTag() && stack.getTag().contains(TAG_LABEL)){
            tooltip.add(Component.literal(stack.getTag().get(TAG_LABEL).getAsString()));
        }
        super.appendHoverText(stack, level, tooltip, tf);
    }
}
