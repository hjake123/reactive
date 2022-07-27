package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.Color;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

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

    public static Power readPower(String s){
        Power ret = null;
        for(RegistryObject<Power> reg : Registration.POWERS.getEntries()){
            if(reg.get().getName().equals(s)){
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
    public static List<Power> getSourcePower(ItemStack i) {
        ArrayList<Power> stack_powers = new ArrayList<>();
        if (i.is(AlchemyTags.verdantSource)) stack_powers.add(Registration.VERDANT_POWER.get());
        if (i.is(AlchemyTags.blazeSource)) stack_powers.add(Registration.BLAZE_POWER.get());
        if (i.is(AlchemyTags.bodySource)) stack_powers.add(Registration.BODY_POWER.get());
        if (i.is(AlchemyTags.curseSource)) stack_powers.add(Registration.CURSE_POWER.get());
        if (i.is(AlchemyTags.lightSource)) stack_powers.add(Registration.LIGHT_POWER.get());
        if (i.is(AlchemyTags.mindSource)) stack_powers.add(Registration.MIND_POWER.get());
        if (i.is(AlchemyTags.soulSource)) stack_powers.add(Registration.SOUL_POWER.get());
        if (i.is(AlchemyTags.vitalSource)) stack_powers.add(Registration.VITAL_POWER.get());
        if (i.is(AlchemyTags.warpSource)) stack_powers.add(Registration.WARP_POWER.get());
        return stack_powers;
    }

    public static int getSourceLevel(ItemStack i, Level level) {
        return WorldSpecificValue.get(
                level, "power_" + i.getItem().getDescriptionId(),
                i.is(AlchemyTags.highPower) ? 100: 10,
                i.is(AlchemyTags.highPower) ? 400: 40);
    }

    @Override
    public String toString(){
        return name;
    }
}
