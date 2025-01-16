package dev.hyperlynx.reactive.integration.jei.bottles;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class PowerBottleRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final Ingredient bottle;
    protected final Power power;

    public PowerBottleRecipe(ResourceLocation id, String group, Power power) {
        this.id = id;
        this.group = group;
        this.bottle = Ingredient.of(power.getBottle());
        this.power = power;
    }

    @Override
    public boolean matches(Container input, Level level) {
        return power.matchesBottle(input.getItem(0));
    }

    @Override
    public ItemStack assemble(Container input, RegistryAccess access) {
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
    public ItemStack getResultItem(RegistryAccess access) {
        return Registration.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public Power getPower() {
        return power;
    }
}
