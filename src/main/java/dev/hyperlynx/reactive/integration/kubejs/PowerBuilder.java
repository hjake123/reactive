package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class PowerBuilder extends BuilderBase<Power> {
    public transient int color;
    public transient Item bottle;
    public transient Item render_item;
    public transient Block render_water_block;

    public PowerBuilder(ResourceLocation id) {
        super(id);
        color = 0xFFFFFF;
        render_item = Items.BARRIER;
        bottle = null;
        render_water_block = Blocks.WATER;
    }

    @Override
    public Power createObject() {
        return new Power(this.id, color, render_water_block, bottle, render_item);
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

    public PowerBuilder setNormalWater(){
        this.render_water_block = Blocks.WATER;
        return this;
    }

    public PowerBuilder setMagicWater(){
        this.render_water_block = Registration.DUMMY_MAGIC_WATER.get();
        return this;
    }
    public PowerBuilder setFastWater(){
        this.render_water_block = Registration.DUMMY_FAST_WATER.get();
        return this;
    }
    public PowerBuilder setNoiseWater(){
        this.render_water_block = Registration.DUMMY_NOISE_WATER.get();
        return this;
    }
    public PowerBuilder setSlowWater(){
        this.render_water_block = Registration.DUMMY_SLOW_WATER.get();
        return this;
    }

    public PowerBuilder setCustomWater(Block water){
        this.render_water_block = water;
        return this;
    }
}
