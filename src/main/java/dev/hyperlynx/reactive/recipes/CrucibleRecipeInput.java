package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CrucibleRecipeInput implements RecipeInput {
    ItemStack item;
    Map<Power, Integer> reagents;
    private int electric_charge = 0;

    public static CrucibleRecipeInput of(ItemStack stack, int charge){
        var input = new CrucibleRecipeInput();
        input.item = stack;
        input.electric_charge = charge;
        return input;
    }

    public static CrucibleRecipeInput of(ItemStack stack, int charge, Map<Power, Integer> powers){
        var input = new CrucibleRecipeInput();
        input.item = stack;
        input.reagents = powers;
        input.electric_charge = charge;
        return input;
    }

    public static CrucibleRecipeInput of(int charge, Map<Power, Integer> powers){
        var input = new CrucibleRecipeInput();
        input.item = ItemStack.EMPTY;
        input.reagents = powers;
        input.electric_charge = charge;
        return input;
    }

    public CrucibleRecipeInput charge(int charge){
        electric_charge = charge;
        return this;
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

    public boolean hasCharge(){
        return electric_charge > 0;
    }
}
