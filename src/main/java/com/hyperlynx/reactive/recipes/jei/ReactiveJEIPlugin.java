package com.hyperlynx.reactive.recipes.jei;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.items.DisplacerItem;
import com.hyperlynx.reactive.items.StaffItem;
import com.hyperlynx.reactive.recipes.DissolveRecipe;
import com.hyperlynx.reactive.util.ConfigMan;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@JeiPlugin
public class ReactiveJEIPlugin implements IModPlugin {
    public static IJeiHelpers HELPERS;
    public static DissolveRecipeCategory DISSOLVE_CATEGORY = new DissolveRecipeCategory();
    public static TransmuteRecipeCategory TRANSMUTE_CATEGORY = new TransmuteRecipeCategory();
    public static PowerIngredientType POWER_TYPE = new PowerIngredientType();
    public static PowerIngredientHandler POWER_HANDLER = new PowerIngredientHandler();
    public static PowerIngredientRenderer POWER_RENDERER = new PowerIngredientRenderer();
    public static IJeiRuntime RUNTIME;

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
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(POWER_TYPE, Powers.POWER_SUPPLIER.get().getValues(), POWER_HANDLER, POWER_RENDERER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        HELPERS = registration.getJeiHelpers();
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        registration.addRecipes(DISSOLVE_CATEGORY.getRecipeType(), level.getRecipeManager().getAllRecipesFor(Registration.DISSOLVE_RECIPE_TYPE.get()));
        registration.addRecipes(TRANSMUTE_CATEGORY.getRecipeType(), level.getRecipeManager().getAllRecipesFor(Registration.TRANS_RECIPE_TYPE.get()));
        addDescriptions(registration);
        addStaffRepairRecipe((StaffItem) Registration.STAFF_OF_BLAZE_ITEM.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe((StaffItem) Registration.STAFF_OF_LIFE_ITEM.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe((StaffItem) Registration.STAFF_OF_LIGHT_ITEM.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe((StaffItem) Registration.STAFF_OF_MIND_ITEM.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe((StaffItem) Registration.STAFF_OF_WARP_ITEM.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe((StaffItem) Registration.STAFF_OF_SOUL_ITEM.get(), registration, registration.getVanillaRecipeFactory());
        addDisplacerRepairRecipe(registration, registration.getVanillaRecipeFactory());
        registration.getIngredientManager().removeIngredientsAtRuntime(POWER_TYPE, Powers.POWER_SUPPLIER.get().getValues());
        if(ConfigMan.CLIENT.showPowerSources.get())
            addPowerSourceRecipes(registration);
    }

    // TODO: this is bad! and slow!
    private void addPowerSourceRecipes(IRecipeRegistration registration){
        Set<Item> excluded = new HashSet<>();
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        List<DissolveRecipe> purify_recipes = level.getRecipeManager().getAllRecipesFor(Registration.DISSOLVE_RECIPE_TYPE.get());
        for (DissolveRecipe r : purify_recipes) {
            for(ItemStack stack: r.getReactant().getItems())
                excluded.add(stack.getItem());
        }

        for(ItemStack i : registration.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK)){
            if(Power.getSourcePower(i).size() > 0 && !excluded.contains(i.getItem())) {
                registration.addRecipes(DISSOLVE_CATEGORY.getRecipeType(), List.of(new DissolveRecipe(
                        null,
                        "power_source",
                        Ingredient.of(i), ItemStack.EMPTY, false)));
            }
        }
    }

    private void addDescriptions(IRecipeRegistration registration) {
        registration.addItemStackInfo(Registration.CRUCIBLE_ITEM.get().getDefaultInstance(), Component.translatable("jei.reactive.crucible"));
        registration.addItemStackInfo(Registration.SALT.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(Registration.GOLD_FOAM_ITEM.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(Registration.MOTION_SALT.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(Registration.SECRET_SCALE.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(Registration.PHANTOM_RESIDUE.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_input"));
        addGenericDescriptions(registration, Registration.STAFF_OF_WARP_ITEM.get(), Registration.STAFF_OF_MIND_ITEM.get(),
                Registration.STAFF_OF_BLAZE_ITEM.get(), Registration.STAFF_OF_LIFE_ITEM.get(), Registration.STAFF_OF_LIGHT_ITEM.get(),
                Registration.STAFF_OF_SOUL_ITEM.get(), Registration.SOLID_PORTAL_ITEM.get(), Registration.LIGHT_BOTTLE.get(),
                Registration.MIND_BOTTLE.get(), Registration.BODY_BOTTLE.get(), Registration.WARP_BOTTLE.get(), Registration.BLAZE_BOTTLE.get(),
                Registration.ACID_BOTTLE.get(), Registration.VERDANT_BOTTLE.get(), Registration.SOUL_BOTTLE.get());
        addPowerDescriptions(registration);
    }

    private void addGenericDescriptions(IRecipeRegistration registration, Item... items){
        for(Item item : items){
            registration.addItemStackInfo(item.getDefaultInstance(), Component.translatable("jei.reactive.generic"));
        }
    }

    private void addPowerDescriptions(IRecipeRegistration registration){
        for(Power power : Powers.POWER_SUPPLIER.get().getValues()){
            registration.addIngredientInfo(power, POWER_TYPE, Component.translatable("jei.reactive.power"));
        }
    }

    private void addStaffRepairRecipe(StaffItem staff, IRecipeRegistration registration, IVanillaRecipeFactory factory){
        ItemStack full_durability = new ItemStack(staff);
        ItemStack three_quarters_durability = new ItemStack(staff);
        three_quarters_durability.setDamageValue(full_durability.getMaxDamage() / 4);
        ItemStack half_durability = new ItemStack(staff);
        half_durability.setDamageValue(full_durability.getMaxDamage() / 2);

        IJeiAnvilRecipe sacrifice_repair_recipe = factory.createAnvilRecipe(half_durability, List.of(half_durability),  List.of(full_durability));
        IJeiAnvilRecipe bottle_repair_recipe = factory.createAnvilRecipe(three_quarters_durability, List.of(new ItemStack(staff.repair_item)),  List.of(full_durability));

        registration.addRecipes(RecipeTypes.ANVIL, List.of(sacrifice_repair_recipe, bottle_repair_recipe));
    }

    private void addDisplacerRepairRecipe(IRecipeRegistration registration, IVanillaRecipeFactory factory){
        Item displacer = Registration.DISPLACER.get();
        ItemStack full_durability = new ItemStack(displacer);
        ItemStack three_quarters_durability = new ItemStack(displacer);
        three_quarters_durability.setDamageValue(full_durability.getMaxDamage() / 4);
        ItemStack half_durability = new ItemStack(displacer);
        half_durability.setDamageValue(full_durability.getMaxDamage() / 2);

        IJeiAnvilRecipe sacrifice_repair_recipe = factory.createAnvilRecipe(half_durability, List.of(half_durability),  List.of(full_durability));
        IJeiAnvilRecipe bottle_repair_recipe = factory.createAnvilRecipe(three_quarters_durability, List.of(Registration.MOTION_SALT.get().getDefaultInstance()),  List.of(full_durability));

        registration.addRecipes(RecipeTypes.ANVIL, List.of(sacrifice_repair_recipe, bottle_repair_recipe));
    }


    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        RUNTIME = jeiRuntime;
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Registration.CRUCIBLE_ITEM.get().getDefaultInstance(), DISSOLVE_CATEGORY.getRecipeType());
        registration.addRecipeCatalyst(Registration.CRUCIBLE_ITEM.get().getDefaultInstance(), TRANSMUTE_CATEGORY.getRecipeType());
    }
}
