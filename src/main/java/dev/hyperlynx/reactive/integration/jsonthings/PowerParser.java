package dev.hyperlynx.reactive.integration.jsonthings;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

public class PowerParser extends ThingParser<JsonPowerBuilder> {
    public static final String THING_TYPE = ReactiveMod.MODID + "/power";

    public PowerParser(IEventBus bus) {
        super(GSON, THING_TYPE);
        bus.addListener(this::onRegisterEvent);
    }

    private void onRegisterEvent(RegisterEvent event){
        event.register(Powers.POWER_REGISTRY_KEY, (helper) -> {
            ReactiveMod.LOGGER.info("Registering thingpacks for {}", THING_TYPE);
            processAndConsumeErrors(THING_TYPE, getBuilders(), (builder) -> helper.register(builder.getRegistryName(), builder.get()), BaseBuilder::getRegistryName);
            ReactiveMod.LOGGER.info("Done registering thingpacks for {}", THING_TYPE);
        });
    }

    @Override
    protected JsonPowerBuilder processThing(ResourceLocation location, JsonObject json, Consumer<JsonPowerBuilder> builder_modification) {
        JsonPowerBuilder builder = new JsonPowerBuilder(this, location);
        JParse.begin(json)
                .ifKey("color", (color) -> builder.color(parseColor(color.obj())))
                .ifKey("bottle", (bottle) -> builder.bottle(DeferredHolder.create(Registries.ITEM, ResourceLocation.parse(bottle.string().getAsString()))))
                .ifKey("render_water_block", (water) -> builder.water(DeferredHolder.create(Registries.BLOCK, ResourceLocation.parse(water.string().getAsString()))))
                .ifKey("invisible", (invisible) -> builder.setInvisible(invisible.bool().getAsBoolean()))
                .ifKey("name_override", (custom_name) -> builder.setName(Component.literal(custom_name.string().getAsString())));
        builder_modification.accept(builder);
        return builder;
    }

}
