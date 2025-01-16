package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CustomPower extends Power {
    MutableComponent custom_component = null;

    public CustomPower(ResourceLocation id, int color, Block render_water_block, Item bottle) {
        super(id, color, render_water_block, bottle);
    }

    @Override
    public String getName() {
        if(custom_component != null){
            return custom_component.getString();
        }
        return super.getName();
    }
}
