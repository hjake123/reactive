package com.hyperlynx.reactive.recipes;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.IPowerBearer;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class TransmuteRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final ItemStack reactant;
    protected final ItemStack product;
    protected final List<Power> reagents;
    int window_width;

    public TransmuteRecipe(ResourceLocation id, String group, ItemStack reactant, ItemStack product, List<Power> reagents, int window_width) {
        this.id = id;
        this.group = group;
        this.reactant = reactant;
        this.product = product;
        this.reagents = reagents;
        this.window_width = window_width;
    }

    public boolean powerMet(IPowerBearer bearer, Level level){
        int min = WorldSpecificValue.get(level, id.getPath() + "_mincost", 1, CrucibleBlockEntity.CRUCIBLE_MAX_POWER - window_width);
        int max = min + window_width;

        System.err.println("[" + min + ", " + max + "]");

        for(Power p : reagents){
            if(bearer.getPowerLevel(p) < min || bearer.getPowerLevel(p) > max){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return container.getItem(0).is(reactant.getItem());
    }

    @Override
    public ItemStack assemble(Container container) {
        return product.copy();
    }

    @Override
    public ItemStack getResultItem() {
        return product;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.TRANS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.TRANS_RECIPE_TYPE.get();
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
