package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.registration.ReactiveRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DissolveRecipe implements Recipe<CrucibleRecipeInput> {
    protected final Ingredient reactant;
    protected final ItemStack product;
    public final boolean needs_electricity;

    public DissolveRecipe(String ignored, Ingredient reactant, ItemStack product, boolean needs_electricity) {
        this.reactant = reactant;
        this.product = product;
        this.needs_electricity = needs_electricity;
    }

    @Override
    public boolean matches(@NotNull CrucibleRecipeInput input, @NotNull Level level) {
        for(ItemStack i : reactant.getItems()) {
            if (input.getItem().is(i.getItem()))
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

    @Override
    public ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return product;
    }

    public ItemStack getProduct() {
        return product;
    }

    public Ingredient getReactant(){ return reactant; }

    public boolean isElectricityRequired(){ return needs_electricity; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ReactiveRecipes.DISSOLVE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ReactiveRecipes.DISSOLVE_RECIPE_TYPE.get();
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
