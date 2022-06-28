package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

// This class represents one of the kinds of Alchemical Power that items can produce when put into the crucible. It's similar to Item.
public class Power {
    private final Color color;
    private final String name;
    public Power(String name, int color){
        this.name = name;
        this.color = new Color(color);
    }

    public Power(String name, Color color){
        this.name = name;
        this.color = color;
    }

    // Searches the Power Registry to locate the power referred to by the name in the tag.
    public static Power readPower(CompoundTag tag){
        Power ret = null;
        for(RegistryObject<Power> reg : Registration.POWERS.getEntries()){
            if(reg.get().getName().equals(tag.getString("name"))){
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
    public String getName() { return name; }

    // Checks if the ItemStack is assigned any of the Power-related tags, and if so, returns which power it is.
    // TODO: Deal with stacks assigned to multiple Powers somehow.
    public static Power getSourcePower(ItemStack i) {
        if (i.is(AlchemyTags.acidSource)) return Registration.ACID_POWER.get();
        if (i.is(AlchemyTags.blazeSource)) return Registration.BLAZE_POWER.get();
        if (i.is(AlchemyTags.bodySource)) return Registration.BODY_POWER.get();
        if (i.is(AlchemyTags.curseSource)) return Registration.CURSE_POWER.get();
        if (i.is(AlchemyTags.lightSource)) return Registration.LIGHT_POWER.get();
        if (i.is(AlchemyTags.mindSource)) return Registration.MIND_POWER.get();
        if (i.is(AlchemyTags.soulSource)) return Registration.SOUL_POWER.get();
        if (i.is(AlchemyTags.vitalSource)) return Registration.VITAL_POWER.get();
        if (i.is(AlchemyTags.warpSource)) return Registration.WARP_POWER.get();
        return null;
    }

    // TODO: Exact yield/power should vary per world.
    public static int getSourcelevel(ItemStack i) {
        return i.is(AlchemyTags.highPower) ? 160 : 10;
    }

}
