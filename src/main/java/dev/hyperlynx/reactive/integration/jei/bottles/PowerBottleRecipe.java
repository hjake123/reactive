package dev.hyperlynx.reactive.integration.jei.bottles;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class PowerBottleRecipe implements Recipe<RecipeInput> {
    protected final String group;
    protected final ResourceKey<Power> power_key;

    public PowerBottleRecipe(String group, ResourceKey<Power> key) {
        this.group = group;
        this.power_key = key;
    }

    public Power power(RegistryAccess access) {
        return Powers.get(power_key, access);
    }

    public Ingredient bottle(RegistryAccess access){
        return Ingredient.of(power(access).getBottle());
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return power(level.registryAccess()).matchesBottle(input.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        return Registration.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.JEI_BOTTLE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.JEI_BOTTLE_RECIPE_TYPE.get();
    }

    // No, these recipes aren't for the recipe book, Mojang...

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return Registration.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public ResourceKey<Power> getPowerKey() {
        return power_key;
    }
}
