package dev.hyperlynx.reactive.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.util.Color;
import dev.hyperlynx.reactive.util.PrimedWSV;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

// This class represents one of the kinds of Alchemical Power that items can produce when put into the crucible. It's similar to Item.
public class Power {
    private final Color color;
    private ResourceLocation location = null;
    private final Supplier<String> name;
    private final Item bottle;
    private final Block render_water_block;
    private final Supplier<PrimedWSV> percent_reactivity;
    public boolean invisible = false;
    public MutableComponent name_override = null;

    public static final Codec<ResourceKey<Power>> RESOURCE_KEY_CODEC;
    public static final StreamCodec<ByteBuf, ResourceKey<Power>> RESOURCE_KEY_STREAM_CODEC;

    public static final Codec<Power> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Color.CODEC.fieldOf("color").forGetter(Power::getColor),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("water_render_block").forGetter(Power::getWaterRenderBlock),
                    BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("bottle").forGetter(Power::getBottleItem),
                    Codec.BOOL.optionalFieldOf("invisible").forGetter(Power::invisibleForCodec),
                    Codec.STRING.optionalFieldOf("literal_name").forGetter(Power::customName)
            ).apply(instance, Power::new)
    );

    // From Data constructor
    public Power(Color color, Block render_water_block, Optional<Item> possible_bottle_holder, Optional<Boolean> is_invisible, Optional<String> custom_name) {
        this.render_water_block = render_water_block;
        this.color = color;
        this.bottle = possible_bottle_holder.orElse(null);
        this.name = () -> Util.makeDescriptionId("power", location());
        this.percent_reactivity = () -> new PrimedWSV(location() + "_reactivity", 50, 200);
        this.invisible = is_invisible.isPresent() && is_invisible.get();
        custom_name.ifPresent(name_override -> this.name_override = Component.literal(name_override));
    }

    // Other constructors for data generation
    public Power(String id, int color, Block render_water_block, Item bottle){
        this.location = ReactiveMod.location(id);
        this.render_water_block = render_water_block;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = () -> Util.makeDescriptionId("power", location());
        this.percent_reactivity = () -> new PrimedWSV(location() + "_reactivity", 50, 200);
    }

    public Power(ResourceLocation location, Color color, Block render_water_block, Item bottle){
        this.location = location;
        this.color = color;
        this.render_water_block = render_water_block;
        this.bottle = bottle;
        this.name = () -> Util.makeDescriptionId("power", location());
        this.percent_reactivity = () -> new PrimedWSV(location() + "_reactivity", 50, 200);
    }

    void setLocation(ResourceLocation location){
        this.location = location;
    }

    public TagKey<Item> getSourceTag(){
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "_sources"));
    }

    // Searches the Power Registry to locate the power referred to by the name in the tag.
    public static Power readPower(CompoundTag tag, HolderLookup.Provider lookup_provider){
        return readPower(tag, "name", lookup_provider);
    }

    public static Power readPower(CompoundTag tag, String power_key, HolderLookup.Provider lookup_provider){
        String rl = tag.getString(power_key);
        var location = ResourceLocation.parse(rl);
        var potential_power = lookup_provider.lookup(Powers.POWER_REGISTRY_KEY).get().get(ResourceKey.create(Powers.POWER_REGISTRY_KEY, location));
        if(potential_power.isEmpty()){
            ReactiveMod.LOGGER.warn("Tried to look up a power {} that did not exist. Ignoring this power...", location.toString());
            return null;
        }
        var power_ref = potential_power.get();
        return power_ref.value();
    }

    public Color getColor(){
        return color;
    }
    public TextColor getTextColor(){
        return TextColor.fromRgb(color.hex);
    }
    public String getId() { return location.getPath(); }

    public String getName() {
        if(name_override != null){
            return name_override.getString();
        }
        return Component.translatable(name.get()).getString();
    }

    public ResourceLocation location() {
        return location;
    }
    public Block getWaterRenderBlock(){
        return render_water_block;
    }

    public Optional<Boolean> invisibleForCodec() {
        if(invisible){
            return Optional.of(invisible);
        }
        return Optional.empty();
    }

    private Optional<String> customName() {
        if(name_override == null){
            return Optional.empty();
        }
        return Optional.of(name_override.getString());
    }

    // Returns whether the given power level is sufficient to cause a reaction with this power.
    public boolean checkReactivity(int power_level, int threshold){
        float strength = percent_reactivity.get().get() / 100F;
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

    public Optional<Item> getBottleItem() {
        if(hasBottle())
            return Optional.of(bottle);
        return Optional.empty();
    }

    @Override
    public String toString(){
        return name.get();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(location);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Power other){
            return other.location.equals(this.location);
        }
        return super.equals(obj);
    }

    static{
        RESOURCE_KEY_CODEC = ResourceKey.codec(Powers.POWER_REGISTRY_KEY);
        RESOURCE_KEY_STREAM_CODEC = ResourceKey.streamCodec(Powers.POWER_REGISTRY_KEY);
    }
}
