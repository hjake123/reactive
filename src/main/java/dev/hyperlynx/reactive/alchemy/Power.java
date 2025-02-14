package dev.hyperlynx.reactive.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import dev.hyperlynx.reactive.util.Color;
import dev.hyperlynx.reactive.util.PrimedWSV;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// This class represents one of the kinds of Alchemical Power that items can produce when put into the crucible. It's similar to Item.
public class Power {
    private final Color color;
    private final ResourceLocation location;
    private final String name;
    private final Item bottle;
    private final Block render_water_block;
    private final PrimedWSV percent_reactivity;
    public boolean invisible = false;

    public static final Codec<ResourceKey<Power>> RESOURCE_KEY_CODEC;
    public static final StreamCodec<ByteBuf, ResourceKey<Power>> RESOURCE_KEY_STREAM_CODEC;

    public static final Codec<Power> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("location").forGetter(Power::getResourceLocation),
                    Color.CODEC.fieldOf("color").forGetter(Power::getColor),
                    Block.CODEC.fieldOf("water_render_block").forGetter(Power::getWaterRenderBlock),
                    ItemStack.ITEM_NON_AIR_CODEC.optionalFieldOf("bottle").forGetter(Power::getBottleItem)
            ).apply(instance, Power::new)
    );

    public Power(String id, int color, Block render_water_block, Item bottle){
        this.location = ReactiveMod.location(id);
        this.render_water_block = render_water_block;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", this.location);
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
    }

    public Power(String id, Color color, Block render_water_block, Item bottle){
        this.location = ReactiveMod.location(id);
        this.render_water_block = render_water_block;
        this.color = color;
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", this.location);
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
    }


    public Power(ResourceLocation location, Color color, Block render_water_block, Item bottle){
        this.location = location;
        this.color = color;
        this.render_water_block = render_water_block;
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", this.location);
        this.percent_reactivity = new PrimedWSV(location + "_reactivity", 50, 200);
    }

    public Power(ResourceLocation location, int color, Block render_water_block, Item bottle){
        this.location = location;
        this.render_water_block = render_water_block;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", this.location);
        this.percent_reactivity = new PrimedWSV(location + "_reactivity", 50, 200);
    }

    public Power(ResourceLocation location, Color color, Block render_water_block, Optional<Holder<Item>> possible_bottle_holder) {
        this.location = location;
        this.render_water_block = render_water_block;
        this.color = color;
        this.bottle = possible_bottle_holder.orElse(Holder.direct(null)).value();
        this.name = Util.makeDescriptionId("power", this.location);
        this.percent_reactivity = new PrimedWSV(location + "_reactivity", 50, 200);
    }

    public TagKey<Item> getSourceTag(){
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "_sources"));
    }

    // Searches the Power Registry to locate the power referred to by the name in the tag.
    public static Power readPower(CompoundTag tag, RegistryAccess access){
        return readPower(tag, "name", access);
    }

    public static Power readPower(CompoundTag tag, String power_key, RegistryAccess access){
        String rl = tag.getString(power_key);
        var location = ResourceLocation.parse(rl);
        return Powers.get(location, access);
    }

    public Color getColor(){
        return color;
    }
    public TextColor getTextColor(){
        return TextColor.fromRgb(color.hex);
    }
    public String getId() { return location.getPath(); }
    public String getName(){
        return Component.translatable(name).getString();
    }
    public ResourceLocation getResourceLocation() { return location; }

    public ResourceKey<Power> getResourceKey() { return ResourceKey.create(Powers.POWER_REGISTRY_KEY, location); }

    public Block getWaterRenderBlock(){
        return render_water_block;
    }

    // Returns whether the given power level is sufficient to cause a reaction with this power.
    public boolean checkReactivity(int power_level, int threshold){
        float strength = percent_reactivity.get() / 100F;
        int adjusted_power_level = (int) (power_level * strength);
        return adjusted_power_level >= threshold;
    }

    // Checks if the ItemStack is assigned any of the auto-assigned Power related tage, and if so, returns which power it is.
    public static List<Power> getSourcePower(RegistryAccess access, ItemStack i) {
        ArrayList<Power> stack_powers = new ArrayList<>();
        Powers.stream(access).forEach((power) -> {
            if (i.is(power.getSourceTag()))
                stack_powers.add(power);
        });
        return stack_powers;
    }

    public static int getSourceLevel(ItemStack i) {
        return WorldSpecificValue.get(
                "power_" + i.getItem().getDescriptionId(),
                i.is(AlchemyTags.highPower) ? 250: 40,
                i.is(AlchemyTags.highPower) ? 500: 90);
    }

    public boolean hasBottle(){
        return bottle != null;
    }

    public boolean matchesBottle(ItemStack i){
        if(hasBottle())
            return i.is(bottle);
        return false;
    }

    public ItemStack getBottle(){
        if(hasBottle())
            return bottle.getDefaultInstance();
        return ItemStack.EMPTY;
    }

    public Optional<Holder<Item>> getBottleItem() {
        if(hasBottle())
            return Optional.of(Holder.direct(bottle));
        return Optional.empty();
    }

    @Override
    public String toString(){
        return name;
    }

    static{
        RESOURCE_KEY_CODEC = ResourceKey.codec(Powers.POWER_REGISTRY_KEY);
        RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(Powers.POWER_REGISTRY_KEY);
    }
}
