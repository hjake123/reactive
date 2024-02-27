package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.util.Color;
import com.hyperlynx.reactive.util.PrimedWSV;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

// This class represents one of the kinds of Alchemical Power that items can produce when put into the crucible. It's similar to Item.
public class Power {
    private final Color color;
    private final String id;
    private final String name;
    private final Item bottle;
    private final Item render_item;
    private final PrimedWSV percent_reactivity;

    public Power(String id, int color, Item bottle){
        this.id = id;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", new ResourceLocation(ReactiveMod.MODID, id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = null;
    }

    public Power(String id, Color color, Item bottle){
        this.id = id;
        this.color = color;
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", new ResourceLocation(ReactiveMod.MODID, id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = null;
    }


    public Power(String id, int color, Item bottle, Item renderItem){
        this.id = id;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", new ResourceLocation(ReactiveMod.MODID, id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = renderItem;
    }

    public Power(String id, Color color, Item bottle, Item renderItem){
        this.id = id;
        this.color = color;
        this.bottle = bottle;
        this.name = Util.makeDescriptionId("power", new ResourceLocation(ReactiveMod.MODID, id));
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 50, 200);
        render_item = renderItem;
    }

    public static TagKey<Item> getSourceTag(String id){
        return ItemTags.create(new ResourceLocation(ReactiveMod.MODID, id + "_sources"));
    }

    // Searches the Power Registry to locate the power referred to by the name in the tag.
    public static Power readPower(CompoundTag tag){
        return readPower(tag, "name");
    }

    public static Power readPower(CompoundTag tag, String power_key){
        Power ret = null;
        for(RegistryObject<Power> reg : Powers.POWERS.getEntries()){
            if(reg.get().getId().equals(tag.getString(power_key))){
                ret = reg.get();
                break;
            }
        }
        if(ret == null) System.err.println("Failed to read power. This will break things.");
        return ret;
    }

    public Color getColor(){
        return color;
    }
    public TextColor getTextColor(){
        return TextColor.fromRgb(color.hex);
    }
    public String getId() { return id; }
    public String getName(){
        return Component.translatable(name).getString();
    }

    // Returns whether the given power level is sufficient to cause a reaction with this power.
    public boolean checkReactivity(int power_level, int threshold){
        float strength = percent_reactivity.get() / 100F;
        int adjusted_power_level = (int) (power_level * strength);
        return adjusted_power_level >= threshold;
    }

    // Checks if the ItemStack is assigned any of the auto-assigned Power related tage, and if so, returns which power it is.
    public static List<Power> getSourcePower(ItemStack i) {
        ArrayList<Power> stack_powers = new ArrayList<>();
        for(RegistryObject<Power> reg : Powers.POWERS.getEntries()){
            if (i.is(Power.getSourceTag(reg.get().id))) stack_powers.add(reg.get());
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
        if(bottle != null)
            return bottle.getDefaultInstance();
        if(render_item != null)
            return render_item.getDefaultInstance();
        return ItemStack.EMPTY;
    }
}
