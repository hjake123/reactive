package com.hyperlynx.reactive.recipes.jei;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JeiPlugin
public class ReactiveJEIPlugin implements IModPlugin {
    public static IJeiHelpers HELPERS;
    public static DissolveRecipeCategory DISSOLVE_CATEGORY = new DissolveRecipeCategory();
    public static TransmuteRecipeCategory TRANSMUTE_CATEGORY = new TransmuteRecipeCategory();

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(ReactiveMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(DISSOLVE_CATEGORY);
        registration.addRecipeCategories(TRANSMUTE_CATEGORY);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        HELPERS = registration.getJeiHelpers();
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        registration.addRecipes(DISSOLVE_CATEGORY.getRecipeType(), level.getRecipeManager().getAllRecipesFor(Registration.DISSOLVE_RECIPE_TYPE.get()));
        registration.addRecipes(TRANSMUTE_CATEGORY.getRecipeType(), level.getRecipeManager().getAllRecipesFor(Registration.TRANS_RECIPE_TYPE.get()));
        addDescriptions(registration);
    }

    private void addDescriptions(IRecipeRegistration registration) {
        registration.addItemStackInfo(Registration.CRUCIBLE_ITEM.get().getDefaultInstance(), Component.translatable("jei.reactive.crucible"));
        registration.addItemStackInfo(Registration.SALT.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(Registration.GOLD_FOAM_ITEM.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        addGenericDescriptions(registration, Registration.STAFF_OF_WARP_ITEM.get(), Registration.STAFF_OF_MIND_ITEM.get(),
                Registration.STAFF_OF_BLAZE_ITEM.get(), Registration.STAFF_OF_LIFE_ITEM.get(), Registration.STAFF_OF_LIGHT_ITEM.get(),
                Registration.STAFF_OF_SOUL_ITEM.get(), Registration.SOLID_PORTAL_ITEM.get(), Registration.LIGHT_BOTTLE.get(),
                Registration.MIND_BOTTLE.get(), Registration.BODY_BOTTLE.get(), Registration.WARP_BOTTLE.get(), Registration.BLAZE_BOTTLE.get(),
                Registration.ACID_BOTTLE.get(), Registration.VERDANT_BOTTLE.get(), Registration.SOUL_BOTTLE.get());
    }

    private void addGenericDescriptions(IRecipeRegistration registration, Item... items){
        for(Item item : items){
            registration.addItemStackInfo(item.getDefaultInstance(), Component.translatable("jei.reactive.generic"));
        }
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Registration.CRUCIBLE_ITEM.get().getDefaultInstance(), DISSOLVE_CATEGORY.getRecipeType());
        registration.addRecipeCatalyst(Registration.CRUCIBLE_ITEM.get().getDefaultInstance(), TRANSMUTE_CATEGORY.getRecipeType());
    }
}
