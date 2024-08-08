package com.hyperlynx.reactive.recipes;

import com.hyperlynx.reactive.alchemy.Power;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CrucibleRecipeInput implements RecipeInput {
    ItemStack item;
    Map<Power, Integer> reagents;

    public static CrucibleRecipeInput of(ItemStack stack){
        var input = new CrucibleRecipeInput();
        input.item = stack;
        return input;
    }

    public static CrucibleRecipeInput of(ItemStack stack, Map<Power, Integer> powers){
        var input = new CrucibleRecipeInput();
        input.item = stack;
        input.reagents = powers;
        return input;
    }

    public static CrucibleRecipeInput of(Map<Power, Integer> powers){
        var input = new CrucibleRecipeInput();
        input.item = ItemStack.EMPTY;
        input.reagents = powers;
        return input;
    }

    public @NotNull ItemStack getItem() {
        return item;
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return getItem();
    }

    public int getPowerLevel(Power p){
        if(reagents.containsKey(p)){
            return reagents.get(p);
        }
        return 0;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty();
    }
}
