package com.hyperlynx.reactive.recipes;

import com.hyperlynx.reactive.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DissolveRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final ItemStack reactant;
    protected final ItemStack product;

    public DissolveRecipe(ResourceLocation id, String group, ItemStack reactant, ItemStack product) {
        this.id = id;
        this.group = group;
        this.reactant = reactant;
        this.product = product;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return container.getItem(0).is(reactant.getItem());
    }

    @Override
    public @NotNull ItemStack assemble(Container container) {
        ItemStack result = product.copy();
        result.setCount(container.getItem(1).getCount());
        return result;
    }

    @Override
    public ItemStack getResultItem() {
        return product;
    }

    public ItemStack getReactant(){ return reactant; }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.DISSOLVE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.DISSOLVE_RECIPE_TYPE.get();
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
