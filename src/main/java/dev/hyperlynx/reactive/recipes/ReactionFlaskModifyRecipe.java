package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveRecipes;
import dev.hyperlynx.reactive.components.ReactionFlaskContents;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ReactionFlaskModifyRecipe extends CustomRecipe {

    @SuppressWarnings("unused")
    public ReactionFlaskModifyRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        boolean has_reaction_flask = false;
        boolean has_volt_cell = false;
        boolean has_duplicate_items = false;
        for(ItemStack stack : input.items()) {
            if(stack.is(ReactiveItems.REACTION_FLASK.get()) && stack.has(ReactiveComponentTypes.REACTION_FLASK_CONTENTS.get())) {
                if(has_reaction_flask) {
                    has_duplicate_items = true;
                } else {
                    has_reaction_flask = true;

                }
            }
            if(stack.is(ReactiveItems.VOLT_CELL.get())) {
                if(has_volt_cell) {
                    has_duplicate_items = true;
                } else {
                    has_volt_cell = true;
                }
            }
        }

        return has_reaction_flask && has_volt_cell && !has_duplicate_items;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        ReactionFlaskContents contents = new ReactionFlaskContents(Map.of(), false);
        for(ItemStack stack : input.items()) {
            if(stack.is(ReactiveItems.REACTION_FLASK.get())) {
                contents = stack.get(ReactiveComponentTypes.REACTION_FLASK_CONTENTS.get());
            }
        }
        if(contents == null) {
            throw new RuntimeException("Reaction flask with no contents was allowed to assemble a flask modify recipe");
        }
        ItemStack flask = ReactiveItems.REACTION_FLASK.get().getDefaultInstance();
        flask.set(ReactiveComponentTypes.REACTION_FLASK_CONTENTS.get(), new ReactionFlaskContents(contents.powers(), true));
        return flask;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining_items = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < remaining_items.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(ReactiveItems.VOLT_CELL.get())) {
                remaining_items.set(i, stack.getItem().getDefaultInstance());
            }
        }

        return remaining_items;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ReactiveRecipes.REACTION_FLASK_MODIFY_RECIPE_SERIALIZER.get();
    }
}
