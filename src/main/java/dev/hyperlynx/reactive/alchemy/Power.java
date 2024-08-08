package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.util.Color;
import dev.hyperlynx.reactive.util.PrimedWSV;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// This class represents one of the kinds of Alchemical Power that items can produce when put into the crucible. It's similar to Item.
public class Power {
    private final Color color;
    private final ResourceLocation location;
    private final String name;
    private final Item bottle;
    private final Item render_item;
    private final Supplier<Block> render_water_block;
    private final PrimedWSV percent_reactivity;
    public boolean invisible = false;

    public Power(String id, Supplier<Block> render_water_block, int color, Item bottle){
        this.location = ReactiveMod.location(id);
        this.render_water_block = render_water_block;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", ReactiveMod.location(id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = null;
    }

    public Power(String id, Supplier<Block> render_water_block, Color color, Item bottle){
        this.location = ReactiveMod.location(id);
        this.render_water_block = render_water_block;
        this.color = color;
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", ReactiveMod.location(id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = null;
    }


    public Power(String id, Supplier<Block> render_water_block, int color, Item bottle, Item renderItem){
        this.location = ReactiveMod.location(id);
        this.render_water_block = render_water_block;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", ReactiveMod.location(id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = renderItem;
    }

    public Power(String id, Supplier<Block> render_water_block, Color color, Item bottle, Item renderItem){
        this.location = ReactiveMod.location(id);
        this.render_water_block = render_water_block;
        this.color = color;
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", ReactiveMod.location(id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = renderItem;
    }

    public Power(ResourceLocation location, int color, Supplier<Block> render_water_block, Item bottle, Item renderItem){
        this.location = location;
        this.color = new Color(color);
        this.render_water_block = render_water_block;
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", this.location);
        this.percent_reactivity = new PrimedWSV(location + "_reactivity", 50, 200);
        render_item = renderItem;
    }

    public static TagKey<Item> getSourceTag(ResourceLocation location){
        return ItemTags.create(new ResourceLocation(location.getNamespace(), location.getPath() + "_sources"));
    }

    // Searches the Power Registry to locate the power referred to by the name in the tag.
    public static Power readPower(CompoundTag tag){
        return readPower(tag, "name");
    }

    public static Power readPower(CompoundTag tag, String power_key){
        String key = tag.getString(power_key);
        ResourceLocation location;
        if(key.contains(":")){
            location = new ResourceLocation(key);
        }else{
            location = ReactiveMod.location(key);
        }
        Power ret = Powers.POWER_SUPPLIER.get().getValue(location);
        if(ret == null) System.err.println("Failed to read power. This will break things.");
        return ret;
    }

    public Block getRenderBlock(){ return render_water_block.get(); }
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
    public ResourceLocation getResourceLocation(){ return location; }

    // Returns whether the given power level is sufficient to cause a reaction with this power.
    public boolean checkReactivity(int power_level, int threshold){
        float strength = percent_reactivity.get() / 100F;
        int adjusted_power_level = (int) (power_level * strength);
        return adjusted_power_level >= threshold;
    }

    // Checks if the ItemStack is assigned any of the auto-assigned Power related tage, and if so, returns which power it is.
    public static List<Power> getSourcePower(ItemStack i) {
        ArrayList<Power> stack_powers = new ArrayList<>();
        for(Power power : Powers.POWER_SUPPLIER.get().getValues()){
            if (i.is(Power.getSourceTag(power.getResourceLocation()))) {
                stack_powers.add(power);
                break;
            }
        }
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

    @Override
    public String toString(){
        return name;
    }

    public ItemStack getRenderStack() {
        if(bottle != null && bottle.getDefaultInstance().getCount() > 0)
            return bottle.getDefaultInstance();
        if(render_item != null && render_item.getDefaultInstance().getCount() > 0)
            return render_item.getDefaultInstance();
        return Items.BARRIER.getDefaultInstance();
    }
}
