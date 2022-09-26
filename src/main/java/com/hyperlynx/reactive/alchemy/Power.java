package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.util.Color;
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
    private final String name;
    private final Item bottle;

    public Power(String name, int color, Item bottle){
        this.name = name;
        this.color = new Color(color);
        this.bottle = bottle;
    }

    public Power(String name, Color color, Item bottle){
        this.name = name;
        this.color = color;
        this.bottle = bottle;
    }

    // Searches the Power Registry to locate the power referred to by the name in the tag.
    public static Power readPower(CompoundTag tag){
        Power ret = null;
        for(RegistryObject<Power> reg : Powers.POWERS.getEntries()){
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

    public static int getSourceLevel(ItemStack i, Level level) {
        return WorldSpecificValue.get(
                level, "power_" + i.getItem().getDescriptionId(),
                i.is(AlchemyTags.highPower) ? 150: 15,
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
