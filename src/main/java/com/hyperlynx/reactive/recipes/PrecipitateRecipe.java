package com.hyperlynx.reactive.recipes;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.PowerBearer;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.util.WorldSpecificValue;
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

public class PrecipitateRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final ItemStack product;
    protected final List<Power> reagents;
    int cost;
    int minimum;
    int reagent_count;

    public PrecipitateRecipe(ResourceLocation id, String group, ItemStack product, List<Power> reagents, int min, int cost, int reagent_count) {
        this.id = id;
        this.group = group;
        this.product = product;
        this.reagents = reagents;
        this.minimum = min;
        this.cost = cost;
        this.reagent_count = reagent_count;
    }

    // If you meet the required power for the first reagent_cost powers in the world specific order, you're good to go.
    public boolean powerMet(PowerBearer bearer, Level level){
        ArrayList<Power> sorted_reagents = WorldSpecificValue.shuffle(level, id + "_reagent_order", reagents);

        int power_level = 0;
        int iterations = 0;
        boolean has_all_reagents = true;
        for(Power p : sorted_reagents) {
            if(iterations > reagent_count)
                break;
            if(bearer.getPowerLevel(p) == 0){
                has_all_reagents = false;
                break;
            }
            power_level += bearer.getPowerLevel(p);
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
    public boolean matches(Container container, @NotNull Level level) {
        return true; // Any container would match the recipe because there's no input ingredient!
    }

    @Override
    public ItemStack assemble(Container container) {
        return product.copy();
    }

    @Override
    public ItemStack getResultItem() {
        return product;
    }

    public List<Power> getReagents(){ return reagents;}

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.PRECIPITATE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.PRECIPITATE_RECIPE_TYPE.get();
    }

    public String toString(){
        return id.toString();
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
