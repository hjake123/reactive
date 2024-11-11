package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DissolveRecipe implements Recipe<CrucibleRecipeInput> {
    protected final Ingredient reactant;
    protected final ItemStack product;
    public boolean needs_electricity;

    public DissolveRecipe(Ingredient reactant, ItemStack product, boolean needs_electricity) {
        this.reactant = reactant;
        this.product = product;
        this.needs_electricity = needs_electricity;
    }

    @Override
    public boolean matches(@NotNull CrucibleRecipeInput input, @NotNull Level level) {
        if(needs_electricity && !input.hasCharge()){
            return false;
        }
        for(Holder<Item> i : reactant.items()) {
            if (input.getItem().is(i))
                return true;
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(CrucibleRecipeInput input, HolderLookup.@NotNull Provider provider) {
        ItemStack result = product.copy();
        result.setCount(product.getCount() * input.getItem().getCount());
        return result;
    }

    public ItemStack getProduct() {
        return product;
    }

    public Ingredient getReactant(){ return reactant; }

    public boolean isElectricityRequired(){ return needs_electricity; }

    @Override
    public RecipeSerializer<? extends Recipe<CrucibleRecipeInput>> getSerializer() {
        return Registration.DISSOLVE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<CrucibleRecipeInput>> getType() {
        return Registration.DISSOLVE_RECIPE_TYPE.get();
    }

    // No, these recipes aren't for the recipe book, Mojang...

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CAMPFIRE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
