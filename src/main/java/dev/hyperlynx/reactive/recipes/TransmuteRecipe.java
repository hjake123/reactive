package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBearer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransmuteRecipe implements Recipe<CrucibleRecipeInput> {
    protected final Ingredient reactant;
    protected final ItemStack product;
    protected final List<Power> reagents;
    int cost;
    int minimum;
    public boolean needs_electricity;

    public TransmuteRecipe(Ingredient reactant, ItemStack product, List<Power> reagents, int min, int cost, boolean needs_electricity) {
        this.reactant = reactant;
        this.product = product;
        this.reagents = reagents;
        this.minimum = min;
        this.cost = cost;
        this.needs_electricity = needs_electricity;
    }

    private boolean powerMet(CrucibleRecipeInput input){
        int power_level = 0;
        boolean has_all_reagents = true;
        for(Power p : reagents) {
            if(input.getPowerLevel(p) == 0){
                has_all_reagents = false;
                break;
            }
            power_level += input.getPowerLevel(p);
        }
        return has_all_reagents && power_level > minimum;
    }

    public ItemStack apply(ItemStack input, PowerBearer bearer) {
        int max_tfs = Integer.MAX_VALUE;
        if(cost > 0) {
            for (Power p : reagents) {
                max_tfs = Math.min(max_tfs, (bearer.getPowerLevel(p) / (cost / reagents.size())));
                bearer.expendPower(p, cost / reagents.size() * input.getCount());
            }
        }
        ItemStack result = product.copy();
        result.setCount(Math.min(input.getCount(), max_tfs)*result.getCount());
        input.setCount(input.getCount() - Math.min(input.getCount(), max_tfs));
        return result;
    }

    @Override
    public boolean matches(@NotNull CrucibleRecipeInput input, @NotNull Level level) {
        if(needs_electricity && !input.hasCharge()){
            return false;
        }
        for(Holder<Item> i : reactant.items()) {
            if (input.getItem().is(i)) {
                return powerMet(input);
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CrucibleRecipeInput input, HolderLookup.@NotNull Provider provider) {
        return product.copy();
    }

    public ItemStack getProduct() {
        return product;
    }

    public Ingredient getReactant(){ return reactant; }

    public List<Power> getReagents(){ return reagents;}

    public int getCost(){ return cost; }

    public int getMinimum(){ return minimum; }

    public boolean isElectricityRequired(){ return needs_electricity; }

    @Override
    public RecipeSerializer<? extends Recipe<CrucibleRecipeInput>> getSerializer() {
        return Registration.TRANS_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CrucibleRecipeInput>> getType() {
        return Registration.TRANS_RECIPE_TYPE.get();
    }

    // No, these recipes aren't for the recipe book, Mojang...

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CAMPFIRE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
