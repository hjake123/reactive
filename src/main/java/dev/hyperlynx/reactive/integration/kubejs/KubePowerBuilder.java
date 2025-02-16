package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.integration.custom.CustomPower;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class KubePowerBuilder extends BuilderBase<Power> {
    public transient int color;
    public transient Item bottle;
    public transient Block render_water_block;
    public transient boolean invisible = false;
    public transient MutableComponent custom_component = null;

    public KubePowerBuilder(ResourceLocation id) {
        super(id);
        color = 0xFFFFFF;
        bottle = null;
        render_water_block = Blocks.WATER;
    }

    @Override
    public Power createObject() {
        CustomPower power = new CustomPower(this.id, color, render_water_block, bottle, this.invisible, custom_component);
        power.invisible = this.invisible;
        power.custom_component = custom_component;
        return power;
    }

    public KubePowerBuilder color(int color){
        this.color = color;
        return this;
    }

    public KubePowerBuilder icon(Item icon){
        ReactiveMod.LOGGER.error("Power {} sets a legacy power icon! This won't have any effect.", this.id);
        return this;
    }

    public KubePowerBuilder bottle(Item bottle){
        if(bottle.getDefaultInstance().isEmpty()){
            ReactiveMod.LOGGER.error("Power {} has an invalid bottle item!", this.id);
            return this;
        }
        this.bottle = bottle;
        return this;
    }

    public KubePowerBuilder setNormalWater(){
        this.render_water_block = Blocks.WATER;
        return this;
    }

    public KubePowerBuilder setMagicWater(){
        this.render_water_block = Registration.DUMMY_MAGIC_WATER.get();
        return this;
    }
    public KubePowerBuilder setFastWater(){
        this.render_water_block = Registration.DUMMY_FAST_WATER.get();
        return this;
    }
    public KubePowerBuilder setNoiseWater(){
        this.render_water_block = Registration.DUMMY_NOISE_WATER.get();
        return this;
    }
    public KubePowerBuilder setSlowWater(){
        this.render_water_block = Registration.DUMMY_SLOW_WATER.get();
        return this;
    }

    public KubePowerBuilder setCustomWater(Block water){
        if(water.defaultBlockState().is(Blocks.AIR)){
            throw new KubeScriptException("Power " + this.id + " has an invalid water block!");
        }
        this.render_water_block = water;
        return this;
    }

    public KubePowerBuilder setInvisible(){
        this.invisible = true;
        return this;
    }

    public KubePowerBuilder setName(MutableComponent name){
        this.custom_component = name;
        return this;
    }
}
