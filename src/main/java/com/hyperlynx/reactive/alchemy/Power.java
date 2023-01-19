package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.util.Color;
import com.hyperlynx.reactive.util.PrimedWSV;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

// This class represents one of the kinds of Alchemical Power that items can produce when put into the crucible. It's similar to Item.
public class Power {
    private final Color color;
    private final String id;
    private final String name;
    private final Item bottle;
    private final PrimedWSV percent_reactivity;

    public Power(String id, int color, Item bottle){
        this.id = id;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = id.substring(0, 1).toUpperCase() + id.substring(1).toLowerCase();
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 33, 200);
    }

    public Power(String id, int color, String name, Item bottle){
        this.id = id;
        this.color = new Color(color);
        this.bottle = bottle;
        this.name = name;
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 33, 200);
    }

    public Power(String id, Color color, String name, Item bottle){
        this.id = id;
        this.color = color;
        this.bottle = bottle;
        this.name = name;
        this.percent_reactivity = new PrimedWSV(id + "_reactivity", 33, 200);
    }

    // Searches the Power Registry to locate the power referred to by the name in the tag.
    public static Power readPower(CompoundTag tag){
        Power ret = null;
        for(RegistryObject<Power> reg : Powers.POWERS.getEntries()){
            if(reg.get().getId().equals(tag.getString("name"))){
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
    public String getId() { return id; }
    public String getName(){return name;}

    // Returns whether the given power level is sufficient to cause a reaction with this power.
    // TODO: didn't work for client so this system doesn't do anything.
    public boolean checkReactivity(Level level, int power_level, int threshold){
        float strength = percent_reactivity.get() / 100F;
        int adjusted_power_level = (int) (power_level * strength);
        return adjusted_power_level >= threshold;
    }

    // Checks if the ItemStack is assigned any of the Power-related tags, and if so, returns which power it is.
    public static List<Power> getSourcePower(ItemStack i) {
        ArrayList<Power> stack_powers = new ArrayList<>();
        if (i.is(AlchemyTags.acidSource)) stack_powers.add(Powers.ACID_POWER.get());
        if (i.is(AlchemyTags.verdantSource)) stack_powers.add(Powers.VERDANT_POWER.get());
        if (i.is(AlchemyTags.curseSource)) stack_powers.add(Powers.CURSE_POWER.get());
        if (i.is(AlchemyTags.lightSource)) stack_powers.add(Powers.LIGHT_POWER.get());
        if (i.is(AlchemyTags.mindSource)) stack_powers.add(Powers.MIND_POWER.get());
        if (i.is(AlchemyTags.soulSource)) stack_powers.add(Powers.SOUL_POWER.get());
        if (i.is(AlchemyTags.vitalSource)) stack_powers.add(Powers.VITAL_POWER.get());
        if (i.is(AlchemyTags.warpSource)) stack_powers.add(Powers.WARP_POWER.get());
        if (i.is(AlchemyTags.bodySource)) stack_powers.add(Powers.BODY_POWER.get());
        return stack_powers;
    }

    public static int getSourceLevel(ItemStack i) {
        return WorldSpecificValue.get(
                "power_" + i.getItem().getDescriptionId(),
                i.is(AlchemyTags.highPower) ? 250: 25,
                i.is(AlchemyTags.highPower) ? 500: 50);
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
}
