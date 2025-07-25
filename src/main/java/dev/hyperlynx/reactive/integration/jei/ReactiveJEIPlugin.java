package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.registration.*;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.components.ReactionFlaskContents;
import dev.hyperlynx.reactive.integration.jei.bottles.PowerBottleRecipe;
import dev.hyperlynx.reactive.integration.jei.bottles.PowerBottleRecipeCategory;
import dev.hyperlynx.reactive.items.StaffItem;
import dev.hyperlynx.reactive.recipes.DissolveRecipe;
import dev.hyperlynx.reactive.recipes.ReactionFlaskCraftingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@JeiPlugin
public class ReactiveJEIPlugin implements IModPlugin {
    public static IJeiHelpers HELPERS;
    public static final DissolveRecipeCategory DISSOLVE_CATEGORY = new DissolveRecipeCategory();
    public static final TransmuteRecipeCategory TRANSMUTE_CATEGORY = new TransmuteRecipeCategory();
    public static final PowerBottleRecipeCategory POWER_BOTTLE_CATEGORY = new PowerBottleRecipeCategory();
    public static final PowerIngredientType POWER_TYPE = new PowerIngredientType();
    public static final PowerIngredientHandler POWER_HANDLER = new PowerIngredientHandler();
    public static final PowerIngredientRenderer POWER_RENDERER = new PowerIngredientRenderer();

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ReactiveMod.location("jei_plugin");
    }

    private void setHelpers(IJeiHelpers helpers){
        if(HELPERS == null){
            HELPERS = helpers;
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        setHelpers(registration.getJeiHelpers());
        registration.addRecipeCategories(DISSOLVE_CATEGORY, TRANSMUTE_CATEGORY, POWER_BOTTLE_CATEGORY);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(POWER_TYPE, Powers.POWER_REGISTRY.stream().toList(), POWER_HANDLER, POWER_RENDERER, Power.CODEC);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        setHelpers(registration.getJeiHelpers());
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        registration.addRecipes(DISSOLVE_CATEGORY.getRecipeType(), level.getRecipeManager().getAllRecipesFor(ReactiveRecipes.DISSOLVE_RECIPE_TYPE.get()));
        registration.addRecipes(TRANSMUTE_CATEGORY.getRecipeType(), level.getRecipeManager().getAllRecipesFor(ReactiveRecipes.TRANS_RECIPE_TYPE.get()));
        addDescriptions(registration);
        addStaffRepairRecipe(ReactiveItems.STAFF_OF_BLAZE.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe(ReactiveItems.STAFF_OF_LIFE.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe(ReactiveItems.STAFF_OF_LIGHT.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe(ReactiveItems.STAFF_OF_MIND.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe(ReactiveItems.STAFF_OF_WARP.get(), registration, registration.getVanillaRecipeFactory());
        addStaffRepairRecipe(ReactiveItems.STAFF_OF_SOUL.get(), registration, registration.getVanillaRecipeFactory());
        addDisplacerRepairRecipe(registration, registration.getVanillaRecipeFactory());
        if(!ConfigMan.CLIENT.listPowersAsIngredients.get())
            registration.getIngredientManager().removeIngredientsAtRuntime(POWER_TYPE, Powers.POWER_REGISTRY.stream().toList());
        addComposterRecipes(registration);
        addPowerBottleRecipes(registration);
        if(ConfigMan.CLIENT.showPowerSources.get())
            addPowerSourceRecipes(registration);
        addReactionFlaskRecipes(registration);
    }

    private void addPowerBottleRecipes(IRecipeRegistration registration){
        registration.addRecipes(POWER_BOTTLE_CATEGORY.getRecipeType(), Powers.POWER_REGISTRY.stream()
                .map((power ->  power.hasBottle() ? new PowerBottleRecipe("power_bottles", power) : null)).filter((recipe) -> !(recipe == null)).toList());
    }

    // TODO: this is bad! and slow!
    private void addPowerSourceRecipes(IRecipeRegistration registration){
        Set<Item> excluded = new HashSet<>();
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        List<RecipeHolder<DissolveRecipe>> purify_recipes = level.getRecipeManager().getAllRecipesFor(ReactiveRecipes.DISSOLVE_RECIPE_TYPE.get());
        for (RecipeHolder<DissolveRecipe> r : purify_recipes) {
            for(ItemStack stack: r.value().getReactant().getItems())
                excluded.add(stack.getItem());
        }

        for(ItemStack i : registration.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK)){
            if(!Power.getSourcePower(i).isEmpty() && !excluded.contains(i.getItem())) {
                registration.addRecipes(DISSOLVE_CATEGORY.getRecipeType(), List.of(new RecipeHolder<>(
                        ReactiveMod.location(i.getDescriptionId() + ".power_release_autogen"),
                        new DissolveRecipe(
                            "power_source",
                            Ingredient.of(i), ItemStack.EMPTY, false))));
            }
        }
    }

    private void addDescriptions(IRecipeRegistration registration) {
        registration.addItemStackInfo(ReactiveItems.CRUCIBLE.get().getDefaultInstance(), Component.translatable("jei.reactive.crucible"));
        registration.addItemStackInfo(ReactiveItems.SALT.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(ReactiveItems.GOLD_FOAM.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(ReactiveItems.MOTION_SALT.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(ReactiveItems.SECRET_SCALE.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_result"));
        registration.addItemStackInfo(ReactiveItems.PHANTOM_RESIDUE.get().getDefaultInstance(), Component.translatable("jei.reactive.reaction_input"));
        addGenericDescriptions(registration, ReactiveItems.STAFF_OF_WARP.get(), ReactiveItems.STAFF_OF_MIND.get(),
                ReactiveItems.STAFF_OF_BLAZE.get(), ReactiveItems.STAFF_OF_LIFE.get(), ReactiveItems.STAFF_OF_LIGHT.get(),
                ReactiveItems.STAFF_OF_SOUL.get(), ReactiveItems.SOLID_PORTAL.get(), ReactiveItems.LIGHT_BOTTLE.get(),
                ReactiveItems.MIND_BOTTLE.get(), ReactiveItems.BODY_BOTTLE.get(), ReactiveItems.WARP_BOTTLE.get(), ReactiveItems.BLAZE_BOTTLE.get(),
                ReactiveItems.ACID_BOTTLE.get(), ReactiveItems.VERDANT_BOTTLE.get(), ReactiveItems.SOUL_BOTTLE.get(), ReactiveItems.VITAL_BOTTLE.get());
        addPowerDescriptions(registration);
    }

    private void addGenericDescriptions(IRecipeRegistration registration, Item... items){
        for(Item item : items){
            registration.addItemStackInfo(item.getDefaultInstance(), Component.translatable("jei.reactive.generic"));
        }
    }

    private void addPowerDescriptions(IRecipeRegistration registration){
        for(Power power : Powers.POWER_REGISTRY.stream().toList()){
            registration.addIngredientInfo(power, POWER_TYPE, Component.translatable("jei.reactive.power"));
        }
    }

    private void addStaffRepairRecipe(StaffItem staff, IRecipeRegistration registration, IVanillaRecipeFactory factory){
        ItemStack full_durability = new ItemStack(staff);
        ItemStack three_quarters_durability = new ItemStack(staff);
        three_quarters_durability.setDamageValue(full_durability.getMaxDamage() / 4);
        ItemStack half_durability = new ItemStack(staff);
        half_durability.setDamageValue(full_durability.getMaxDamage() / 2);

        IJeiAnvilRecipe sacrifice_repair_recipe = factory.createAnvilRecipe(half_durability, List.of(half_durability),  List.of(full_durability), ReactiveMod.location("staff_sacrifice_repair"));
        IJeiAnvilRecipe bottle_repair_recipe = factory.createAnvilRecipe(three_quarters_durability, List.of(new ItemStack(staff.repair_item)),  List.of(full_durability), ReactiveMod.location("staff_bottle_repair"));

        registration.addRecipes(RecipeTypes.ANVIL, List.of(sacrifice_repair_recipe, bottle_repair_recipe));
    }

    private void addComposterRecipes(IRecipeRegistration registration){
        registration.addRecipes(RecipeTypes.COMPOSTING, List.of(
                new HyperComposterRecipe(ReactiveItems.VERDANT_BOTTLE),
                new HyperComposterRecipe(ReactiveItems.FLOWER_VINES)
        ));
    }

    private void addDisplacerRepairRecipe(IRecipeRegistration registration, IVanillaRecipeFactory factory){
        Item displacer = ReactiveItems.DISPLACER.get();
        ItemStack full_durability = new ItemStack(displacer);
        ItemStack three_quarters_durability = new ItemStack(displacer);
        three_quarters_durability.setDamageValue(full_durability.getMaxDamage() / 4);
        ItemStack half_durability = new ItemStack(displacer);
        half_durability.setDamageValue(full_durability.getMaxDamage() / 2);

        IJeiAnvilRecipe sacrifice_repair_recipe = factory.createAnvilRecipe(half_durability, List.of(half_durability),  List.of(full_durability), ReactiveMod.location("displacer_sacrifice_repair"));
        IJeiAnvilRecipe bottle_repair_recipe = factory.createAnvilRecipe(three_quarters_durability, List.of(ReactiveItems.MOTION_SALT.get().getDefaultInstance()),  List.of(full_durability), ReactiveMod.location("displacer_salt_repair"));

        registration.addRecipes(RecipeTypes.ANVIL, List.of(sacrifice_repair_recipe, bottle_repair_recipe));
    }

    private void addReactionFlaskRecipes(IRecipeRegistration registration) {
        Map<Character, Ingredient> alphabet = Map.of(
                'c', Ingredient.of(ReactiveItems.INERT_CRYSTAL.get()),
                'b', Ingredient.of(ReactionFlaskCraftingRecipe.POWER_BOTTLE_TAG),
                't', Ingredient.of(ReactiveItems.GOLD_THREAD.get())
                );
        String top = " c ";
        String bottom = " t ";

        ItemStack result_flask = ReactiveItems.REACTION_FLASK.get().getDefaultInstance();
        result_flask.set(ReactiveComponentTypes.REACTION_FLASK_CONTENTS.get(), new ReactionFlaskContents(Map.of(), false));

        registration.addRecipes(RecipeTypes.CRAFTING, List.of(
                new RecipeHolder<>(ReactiveMod.location("special_crafting_recipe_flask_small"),
                        new ShapedRecipe("reactive:special_crafting_recipe_flask", CraftingBookCategory.MISC,
                                ShapedRecipePattern.of(alphabet, List.of(top, " b ", bottom)),
                                result_flask)),

                new RecipeHolder<>(ReactiveMod.location("special_crafting_recipe_flask_medium"),
                        new ShapedRecipe("reactive:special_crafting_recipe_flask", CraftingBookCategory.MISC,
                                ShapedRecipePattern.of(alphabet, List.of(top, "bb ", bottom)),
                                result_flask)),

                new RecipeHolder<>(ReactiveMod.location("special_crafting_recipe_flask_large"),
                        new ShapedRecipe("reactive:special_crafting_recipe_flask", CraftingBookCategory.MISC,
                                ShapedRecipePattern.of(alphabet, List.of(top, "bbb", bottom)),
                                result_flask)),

        new RecipeHolder<>(ReactiveMod.location("special_crafting_recipe_flask_charge"),
                new ShapelessRecipe("reactive:special_crafting_recipe_flask", CraftingBookCategory.MISC,
                        result_flask,
                        NonNullList.of(Ingredient.EMPTY,
                                Ingredient.of(ReactiveItems.REACTION_FLASK.get()),
                                Ingredient.of(ReactiveItems.VOLT_CELL.get()))))
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ReactiveItems.CRUCIBLE.get().getDefaultInstance(), DISSOLVE_CATEGORY.getRecipeType());
        registration.addRecipeCatalyst(ReactiveItems.CRUCIBLE.get().getDefaultInstance(), TRANSMUTE_CATEGORY.getRecipeType());
    }
}
