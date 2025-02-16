package dev.hyperlynx.reactive.integration.jsonthings;

import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.integration.custom.CustomPower;
import dev.hyperlynx.reactive.integration.custom.InvalidCustomObjectException;
import dev.hyperlynx.reactive.integration.kubejs.KubeScriptException;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class JsonPowerBuilder extends BaseBuilder<Power, JsonPowerBuilder> {
    public ResourceLocation id;
    public transient int color;
    public transient Holder<Item> bottle;
    public transient Holder<Block> render_water_block;
    public transient boolean invisible = false;
    public transient MutableComponent custom_component = null;

    public JsonPowerBuilder(ThingParser<JsonPowerBuilder> power_parser, ResourceLocation id) {
        super(power_parser, id);
        this.id = id;
        color = 0xFFFFFF;
        bottle = null;
        render_water_block = Holder.direct(Blocks.WATER);
    }

    @Override
    protected String getThingTypeDisplayName() {
        return Component.translatable("reactive.configuration.power").getString();
    }

    @Override
    protected Power buildInternal() {
        if(bottle != null && bottle.value().getDefaultInstance().isEmpty()){
            ReactiveMod.LOGGER.error("Power {} has an invalid bottle item!", this.id);
        }
        if(render_water_block != null && render_water_block.value().defaultBlockState().is(Blocks.AIR)){
            throw new InvalidCustomObjectException("Power " + this.id + " has an invalid water block!");
        }
        return new CustomPower(id, color, render_water_block == null ? null : render_water_block.value(), bottle == null ? null : bottle.value(), invisible, custom_component);
    }

    public JsonPowerBuilder color(int color){
        this.color = color;
        return this;
    }

    public JsonPowerBuilder bottle(Holder<Item> bottle){
        this.bottle = bottle;
        return this;
    }

    public JsonPowerBuilder setCustomWater(Holder<Block> water){
        this.render_water_block = water;
        return this;
    }

    public JsonPowerBuilder setName(MutableComponent name){
        this.custom_component = name;
        return this;
    }
}
