package com.hyperlynx.reactive.recipes;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.PowerBearer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransmuteRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final ItemStack reactant;
    protected final ItemStack product;
    protected final List<Power> reagents;
    int cost;
    int minimum;
    public boolean needs_electricity;

    public TransmuteRecipe(ResourceLocation id, String group, ItemStack reactant, ItemStack product, List<Power> reagents, int min, int cost, boolean needs_electricity) {
        this.id = id;
        this.group = group;
        this.reactant = reactant;
        this.product = product;
        this.reagents = reagents;
        this.minimum = min;
        this.cost = cost;
        this.needs_electricity = needs_electricity;
    }

    public boolean powerMet(PowerBearer bearer){
        int power_level = 0;
        boolean has_all_reagents = true;
        for(Power p : reagents) {
            if(bearer.getPowerLevel(p) == 0){
                has_all_reagents = false;
                break;
            }
            power_level += bearer.getPowerLevel(p);
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
    public boolean matches(Container container, @NotNull Level level) {
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

    public ItemStack getReactant(){ return reactant; }

    public List<Power> getReagents(){ return reagents;}

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
