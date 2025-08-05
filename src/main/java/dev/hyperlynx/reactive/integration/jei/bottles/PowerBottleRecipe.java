package dev.hyperlynx.reactive.integration.jei.bottles;

import dev.hyperlynx.reactive.registration.ReactiveRecipes;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class PowerBottleRecipe implements Recipe<RecipeInput> {
    protected final Ingredient bottle;
    protected final Power power;

    public PowerBottleRecipe(String ignored, Power power) {
        this.bottle = Ingredient.of(power.getBottle());
        this.power = power;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return power.matchesBottle(input.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        return ReactiveItems.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ReactiveRecipes.JEI_BOTTLE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ReactiveRecipes.JEI_BOTTLE_RECIPE_TYPE.get();
    }

    // No, these recipes aren't for the recipe book, Mojang...

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ReactiveItems.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public Power getPower() {
        return power;
    }
}
