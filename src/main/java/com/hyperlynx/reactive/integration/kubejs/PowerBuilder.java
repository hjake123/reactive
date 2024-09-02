package com.hyperlynx.reactive.integration.kubejs;

import com.hyperlynx.reactive.alchemy.Power;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class PowerBuilder extends BuilderBase<Power> {
    public transient int color;
    public transient Item bottle;
    public transient Item render_item;

    public PowerBuilder(ResourceLocation id) {
        super(id);
        color = 0xFFFFFF;
        render_item = Items.BARRIER;
        bottle = null;
    }

    @Override
    public Power createObject() {
        return new Power(this.id, color, bottle, render_item);
    }

    public PowerBuilder color(int color){
        this.color = color;
        return this;
    }

    public PowerBuilder icon(Item icon){
        this.render_item = icon;
        return this;
    }

    public PowerBuilder bottle(Item bottle){
        this.bottle = bottle;
        return this;
    }
}
