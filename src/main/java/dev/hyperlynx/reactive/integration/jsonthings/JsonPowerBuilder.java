package dev.hyperlynx.reactive.integration.jsonthings;

import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class JsonPowerBuilder extends BaseBuilder<Power, JsonPowerBuilder> {
    private final PowerBuilder builder;

    public JsonPowerBuilder(ThingParser<JsonPowerBuilder> power_parser, ResourceLocation id) {
        super(power_parser, id);
        this.builder = new PowerBuilder(id);
    }

    @Override
    protected @NotNull String getThingTypeDisplayName() {
        return Component.translatable("reactive.configuration.power").getString();
    }

    @Override
    protected @NotNull Power buildInternal() {
        return builder.build();
    }

    public JsonPowerBuilder color(int color){
        builder.color(color);
        return this;
    }

    public JsonPowerBuilder bottle(RegistryObject<Item> bottle){
        builder.bottle(bottle.get());
        return this;
    }

    public JsonPowerBuilder water(RegistryObject<Block> water){
        builder.water(water);
        return this;
    }

    public JsonPowerBuilder setName(MutableComponent name){
        builder.setName(name);
        return this;
    }

    public void setInvisible(boolean invisible) {
        builder.invisible = invisible;
    }
}
