package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CustomPower extends Power {
    MutableComponent custom_component = null;

    public CustomPower(ResourceLocation id, int color, Block render_water_block, Item bottle, Item renderItem) {
        super(id, color, render_water_block, bottle, renderItem);
    }

    @Override
    public String getName() {
        if(custom_component != null){
            return custom_component.getString();
        }
        return super.getName();
    }
}
