package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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

    // Queries the Power Registry to ask for the power referred to by reactive:(name), where (name) is supplied by the tag.
    public static Power readPower(CompoundTag tag){
        return Registration.POWER_SUPPLIER.get().getValue(new ResourceLocation(tag.getString("name"), ReactiveMod.MODID));
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
