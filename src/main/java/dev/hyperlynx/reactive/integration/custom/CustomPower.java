package dev.hyperlynx.reactive.integration.custom;

import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CustomPower extends Power {
    public MutableComponent custom_component;

    public CustomPower(ResourceLocation id, int color, Block render_water_block, Item bottle, boolean invisible, MutableComponent custom_component) {
        super(id, color, render_water_block, bottle);
        this.invisible = invisible;
        this.custom_component = custom_component;
    }

    @Override
    public String getName() {
        if(custom_component != null){
            return custom_component.getString();
        }
        return super.getName();
    }
}
