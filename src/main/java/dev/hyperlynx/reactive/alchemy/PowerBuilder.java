package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class PowerBuilder {
    public final ResourceLocation id;
    public transient int color;
    public transient Holder<Item> bottle_holder;
    public transient Holder<Block> water_holder;
    public transient Item bottle;
    public transient Block water;
    public transient boolean invisible = false;
    public transient MutableComponent custom_component = null;

    public PowerBuilder(ResourceLocation id) {
        this.id = id;
        color = 0xFFFFFF;
        water = Blocks.WATER;
    }

    public PowerBuilder color(int color){
        this.color = color;
        return this;
    }

    public PowerBuilder bottle(Holder<Item> bottle){
        this.bottle_holder = bottle;
        return this;
    }

    public PowerBuilder bottle(Item bottle){
        this.bottle = bottle;
        return this;
    }

    public PowerBuilder water(Holder<Block> water){
        this.water_holder = water;
        return this;
    }

    public PowerBuilder water(Block water){
        this.water = water;
        return this;
    }

    public PowerBuilder setName(MutableComponent name){
        this.custom_component = name;
        return this;
    }

    public Power build() {
        if(bottle_holder != null){
            bottle = bottle_holder.value();
        }
        if(water_holder != null){
            water = water_holder.value();
        }
        if(bottle != null && bottle.getDefaultInstance().isEmpty()){
            ReactiveMod.LOGGER.error("Power {} has an invalid bottle item!", this.id);
        }
        if(water != null && water.defaultBlockState().is(Blocks.AIR)) {
            throw new InvalidPowerBuilderParameterException("Power " + this.id + " has an invalid water block!");
        }
        Power power = new Power(id, color, water, bottle);
        power.invisible = invisible;
        power.name_override = custom_component;
        return power;
    }

    public static class InvalidPowerBuilderParameterException extends RuntimeException {
        public InvalidPowerBuilderParameterException(String msg) {
            super(msg);
        }
    }
}
