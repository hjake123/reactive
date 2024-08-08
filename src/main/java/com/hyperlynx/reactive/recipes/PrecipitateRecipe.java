package com.hyperlynx.reactive.recipes;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.PowerBearer;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PrecipitateRecipe implements Recipe<CrucibleRecipeInput> {
    protected final String group;
    protected final ItemStack product;
    protected final List<Power> reagents;
    int cost;
    int minimum;
    int reagent_count;
    public boolean needs_electricity;

    public PrecipitateRecipe(String group, ItemStack product, List<Power> reagents, int min, int cost, int reagent_count, boolean needs_electricity) {
        this.group = group;
        this.product = product;
        this.reagents = reagents;
        this.minimum = min;
        this.cost = cost;
        this.reagent_count = reagent_count;
        this.needs_electricity = needs_electricity;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    // If you meet the required power for the first reagent_cost powers in the world specific order, you're good to go.
    private boolean powerMet(CrucibleRecipeInput input, Level level){
        ArrayList<Power> sorted_reagents = WorldSpecificValue.shuffle(reagents.hashCode() + "-" + product.hashCode() + "_reagent_order", reagents);

        int power_level = 0;
        int iterations = 0;
        boolean has_all_reagents = true;
        for(Power p : sorted_reagents) {
            if(iterations > reagent_count)
                break;
            if(input.getPowerLevel(p) == 0){
                has_all_reagents = false;
                break;
            }
            power_level += input.getPowerLevel(p);
            iterations++;
        }
        return has_all_reagents && power_level > minimum;
    }

    public ItemStack apply(PowerBearer bearer, Level level) {
        if(cost > 0) {
            for (Power p : reagents) {
                bearer.expendPower(p, cost / reagent_count);
            }
        }
        ItemStack result = product.copy();
        result.setCount(result.getCount());
        return result;
    }

    @Override
    public boolean matches(@NotNull CrucibleRecipeInput input, @NotNull Level level) {
        return powerMet(input, level); // Only power levels are relevant.
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CrucibleRecipeInput input, HolderLookup.@NotNull Provider provider) {
        return product.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return product;
    }

    public List<Power> getReagents(){ return reagents;}

    public int getMinimum() {
        return minimum;
    }

    public int getReagentCount() {
        return reagent_count;
    }

    public boolean isElectricityRequired() {
        return needs_electricity;
    }

    public int getCost() {
        return cost;
    }

    public ItemStack getProduct() {
        return product;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.PRECIPITATE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.PRECIPITATE_RECIPE_TYPE.get();
    }

    // No, these recipes aren't for the recipe book, Mojang...

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
