package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

    //TODO: implement
    public static Power getSourcePower(ItemStack i){
        return Registration.ACID_POWER.get();
    }

}
