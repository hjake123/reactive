package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.integration.jei.bottles.PowerBottleRecipe;
import dev.hyperlynx.reactive.integration.jei.bottles.PowerBottleRecipeSerializer;
import dev.hyperlynx.reactive.recipes.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public class ReactiveRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ReactiveMod.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ReactionFlaskModifyRecipe>> REACTION_FLASK_MODIFY_RECIPE_TYPE = RECIPE_TYPES.register("crafting_special_reaction_flask_modify", () -> getRecipeType("crafting_special_reaction_flask_modify"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ReactionFlaskModifyRecipe>> REACTION_FLASK_MODIFY_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_reaction_flask_modify", () -> new SimpleCraftingRecipeSerializer<>(ReactionFlaskModifyRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<ReactionFlaskCraftingRecipe>> REACTION_FLASK_RECIPE_TYPE = RECIPE_TYPES.register("crafting_special_reaction_flask", () -> getRecipeType("crafting_special_reaction_flask"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ReactionFlaskCraftingRecipe>> REACTION_FLASK_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_reaction_flask", () -> new SimpleCraftingRecipeSerializer<>(ReactionFlaskCraftingRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<PowerBottleRecipe>> JEI_BOTTLE_RECIPE_TYPE = RECIPE_TYPES.register("power_bottle", () -> getRecipeType("power_bottle"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PowerBottleRecipe>> JEI_BOTTLE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("power_bottle", PowerBottleRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<PrecipitateRecipe>> PRECIPITATE_RECIPE_TYPE = RECIPE_TYPES.register("precipitation", () -> getRecipeType("precipitation"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PrecipitateRecipe>> PRECIPITATE_SERIALIZER = RECIPE_SERIALIZERS.register("precipitation", PrecipitateRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DissolveRecipe>> DISSOLVE_RECIPE_TYPE = RECIPE_TYPES.register("dissolve", () -> getRecipeType("dissolve"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DissolveRecipe>> DISSOLVE_SERIALIZER = RECIPE_SERIALIZERS.register("dissolve", DissolveRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TransmuteRecipe>> TRANS_RECIPE_TYPE = RECIPE_TYPES.register("transmutation", () -> getRecipeType("transmutation"));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TransmuteRecipe>> TRANS_SERIALIZER = RECIPE_SERIALIZERS.register("transmutation", TransmuteRecipeSerializer::new);

    // Helper method for Recipe Types.
    public static <T extends Recipe<?>> RecipeType<T> getRecipeType(final String id) {
        return new RecipeType<>()
        {
            public String toString() {
                return ReactiveMod.MODID + ":" + id;
            }
        };
    }
}
