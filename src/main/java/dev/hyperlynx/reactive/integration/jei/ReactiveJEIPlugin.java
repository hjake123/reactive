package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.AlchemyTags;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.integration.jei.bottles.PowerBottleRecipe;
import dev.hyperlynx.reactive.integration.jei.bottles.PowerBottleRecipeCategory;
import dev.hyperlynx.reactive.items.MaterialItem;
import dev.hyperlynx.reactive.items.ReactionFlaskItem;
import dev.hyperlynx.reactive.items.StaffItem;
import dev.hyperlynx.reactive.recipes.DissolveRecipe;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.recipes.TransmuteRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@JeiPlugin
public class ReactiveJEIPlugin implements IModPlugin {
    public static IJeiHelpers HELPERS;
    public static DissolveRecipeCategory DISSOLVE_CATEGORY = new DissolveRecipeCategory();
    public static TransmuteRecipeCategory TRANSMUTE_CATEGORY = new TransmuteRecipeCategory();
    public static PowerBottleRecipeCategory POWER_BOTTLE_CATEGORY = new PowerBottleRecipeCategory();
    public static PowerIngredientType POWER_TYPE = new PowerIngredientType();
    public static PowerIngredientHandler POWER_HANDLER = new PowerIngredientHandler();
    public static PowerIngredientRenderer POWER_RENDERER = new PowerIngredientRenderer();
    public static IJeiRuntime RUNTIME;

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
        registration.addRecipeCategories(DISSOLVE_CATEGORY);
        registration.addRecipeCategories(TRANSMUTE_CATEGORY);
        registration.addRecipeCategories(POWER_BOTTLE_CATEGORY);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(POWER_TYPE, Powers.POWER_SUPPLIER.get().getValues(), POWER_HANDLER, POWER_RENDERER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        setHelpers(registration.getJeiHelpers());
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
        if(!ConfigMan.CLIENT.listPowersAsIngredients.get())
            registration.getIngredientManager().removeIngredientsAtRuntime(POWER_TYPE, Powers.POWER_SUPPLIER.get().getValues());
        addPowerBottleRecipes(registration);
        if(ConfigMan.CLIENT.showPowerSources.get())
            addPowerSourceRecipes(registration);
    }

    private void addPowerBottleRecipes(IRecipeRegistration registration){
        registration.addRecipes(POWER_BOTTLE_CATEGORY.getRecipeType(), Powers.POWER_SUPPLIER.get().getValues().stream()
                .map((power -> power.hasBottle() ? new PowerBottleRecipe(ReactiveMod.location("bottle_of_" + power.getId()),"power_bottles", power) : null)).filter((recipe) -> !(recipe == null)).toList());
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
            if(!Power.getSourcePower(i).isEmpty() && !excluded.contains(i.getItem())) {
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
                Registration.ACID_BOTTLE.get(), Registration.VERDANT_BOTTLE.get(), Registration.SOUL_BOTTLE.get(),
                Registration.VITAL_BOTTLE.get(), Registration.MNEMONIC_BULB_ITEM.get());
        addPowerDescriptions(registration);
        addMaterialRecipes(registration);
        addReactionFlaskRecipes(registration);
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

    private void addReactionFlaskRecipes(IRecipeRegistration registration) {
        ItemStack result_flask = Registration.REACTION_FLASK_ITEM.get().getDefaultInstance();
        ReactionFlaskItem.Contents empty_contents = new ReactionFlaskItem.Contents(Map.of(), false);
        empty_contents.saveToStack(result_flask);

        ItemStack result_flask_charged = result_flask.copy();
        ReactionFlaskItem.Contents charged_contents = new ReactionFlaskItem.Contents(Map.of(), true);
        charged_contents.saveToStack(result_flask_charged);

        registration.addRecipes(RecipeTypes.CRAFTING, List.of(
                new ShapedRecipe(ReactiveMod.location("single_flask_craft"), "reactive:special_crafting_recipe_flask", CraftingBookCategory.MISC,
                                3, 3,
                                NonNullList.of(Ingredient.EMPTY,
                                        Ingredient.EMPTY, Ingredient.of(Registration.INERT_CRYSTAL.get()), Ingredient.EMPTY,
                                        Ingredient.EMPTY, Ingredient.of(AlchemyTags.powerBottles), Ingredient.EMPTY,
                                        Ingredient.EMPTY, Ingredient.of(Registration.GOLD_THREAD.get()), Ingredient.EMPTY),
                                result_flask),

                new ShapedRecipe(ReactiveMod.location("two_flask_craft"), "reactive:special_crafting_recipe_flask", CraftingBookCategory.MISC,
                        3, 3,
                        NonNullList.of(Ingredient.EMPTY,
                                Ingredient.EMPTY, Ingredient.of(Registration.INERT_CRYSTAL.get()), Ingredient.EMPTY,
                                Ingredient.of(AlchemyTags.powerBottles), Ingredient.of(AlchemyTags.powerBottles), Ingredient.EMPTY,
                                Ingredient.EMPTY, Ingredient.of(Registration.GOLD_THREAD.get()), Ingredient.EMPTY),
                        result_flask),

                new ShapedRecipe(ReactiveMod.location("three_flask_craft"), "reactive:special_crafting_recipe_flask", CraftingBookCategory.MISC,
                        3, 3,
                        NonNullList.of(Ingredient.EMPTY,
                                Ingredient.EMPTY, Ingredient.of(Registration.INERT_CRYSTAL.get()), Ingredient.EMPTY,
                                Ingredient.of(AlchemyTags.powerBottles), Ingredient.of(AlchemyTags.powerBottles), Ingredient.of(AlchemyTags.powerBottles),
                                Ingredient.EMPTY, Ingredient.of(Registration.GOLD_THREAD.get()), Ingredient.EMPTY),
                        result_flask),

                new ShapelessRecipe(ReactiveMod.location("special_crafting_recipe_flask"),
                        "special_crafting_recipe_flask",
                        CraftingBookCategory.MISC,
                        result_flask_charged,
                        NonNullList.of(Ingredient.EMPTY,
                                Ingredient.of(result_flask),
                                Ingredient.of(Registration.VOLT_CELL.get())))
        ));
    }

    private void addMaterialRecipes(IRecipeRegistration registration) {
//        ListTag lore_list_tag = new ListTag();
//        lore_list_tag.add(StringTag.valueOf(Component.translatable("text.reactive.jei_material_tooltip").getString()));
//        lore_list_tag.add(StringTag.valueOf(Component.translatable("text.reactive.jei_material_tooltip_1").getString()));
//        lore_list_tag.add(StringTag.valueOf(Component.translatable("text.reactive.jei_material_tooltip_2").getString()));
//        CompoundTag lore_tag = new CompoundTag();
//        lore_tag.put("Lore", lore_list_tag);
// Can't find a good way to get this to work! Seems like a JEI issue...

        ItemStack salt_material_example = Registration.MATERIAL_ITEM.get().getDefaultInstance();
        MaterialItem.setMaterialId(salt_material_example, ReactiveMod.location("example_salt"));

        ItemStack adept_salt_material_example = Registration.MATERIAL_ITEM.get().getDefaultInstance();
        MaterialItem.setMaterialId(adept_salt_material_example, ReactiveMod.location("example_adept_salt"));

        ItemStack creation_salt_material_example = Registration.MATERIAL_ITEM.get().getDefaultInstance();
        MaterialItem.setMaterialId(creation_salt_material_example, ReactiveMod.location("example_creation_salt"));

        ItemStack wool_material_example = Registration.MATERIAL_ITEM.get().getDefaultInstance();
        MaterialItem.setMaterialId(wool_material_example, ReactiveMod.location("example_wool"));

        registration.addRecipes(TRANSMUTE_CATEGORY.getRecipeType(), List.of(
                new TransmuteRecipe(
                        ReactiveMod.location("material_crafting_demo.salt"),
                        "material_crafting_demo",
                        Ingredient.of(Registration.SALT_BLOCK.get()),
                        salt_material_example,
                        List.of(Powers.MIND_POWER.get()),
                        10, 10, false
                ),
                new TransmuteRecipe(
                        ReactiveMod.location("material_crafting_demo.adept_salt"),
                        "material_crafting_demo",
                        Ingredient.of(Registration.ADEPT_SALT_BLOCK.get()),
                        adept_salt_material_example,
                        List.of(Powers.SOUL_POWER.get()),
                                10, 10, false
                ),
                new TransmuteRecipe(
                        ReactiveMod.location("material_crafting_demo.creation_salt"),
                        "material_crafting_demo",
                        Ingredient.of(Registration.CREATION_SALT_BLOCK.get()),
                        creation_salt_material_example,
                        List.of(Powers.WARP_POWER.get()),
                        10, 10, false
                ),
                new TransmuteRecipe(
                        ReactiveMod.location("material_crafting_demo.wool"),
                        "material_crafting_demo",
                        Ingredient.of(Items.WHITE_WOOL),
                        wool_material_example,
                        List.of(Powers.LIGHT_POWER.get()),
                        10, 10, false
                )
        ));
    }
}
