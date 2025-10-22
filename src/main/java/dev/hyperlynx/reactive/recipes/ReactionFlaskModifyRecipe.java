package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.items.ReactionFlaskItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ReactionFlaskModifyRecipe extends CustomRecipe {

    public ReactionFlaskModifyRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer input, @NotNull Level level) {
        boolean has_reaction_flask = false;
        boolean has_volt_cell = false;
        boolean has_duplicate_items = false;
        for(ItemStack stack : input.getItems()) {
            if(stack.is(Registration.REACTION_FLASK_ITEM.get()) && ReactionFlaskItem.Contents.hasContents(stack)) {
                if(has_reaction_flask) {
                    has_duplicate_items = true;
                } else {
                    has_reaction_flask = true;

                }
            }
            if(stack.is(Registration.VOLT_CELL_ITEM.get())) {
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
    public @NotNull ItemStack assemble(@NotNull CraftingContainer input, RegistryAccess pRegistryAccess) {
        ReactionFlaskItem.Contents contents = new ReactionFlaskItem.Contents(Map.of(), false);
        for(ItemStack stack : input.getItems()) {
            if(stack.is(Registration.REACTION_FLASK_ITEM.get())) {
                contents = ReactionFlaskItem.Contents.getFromStack(stack);
            }
        }
        if(contents == null) {
            throw new RuntimeException("Reaction flask with no contents was allowed to assemble a flask modify recipe");
        }
        ItemStack flask = Registration.REACTION_FLASK_ITEM.get().getDefaultInstance();
        ReactionFlaskItem.Contents new_contents = new ReactionFlaskItem.Contents(contents.powers(), true);
        new_contents.saveToStack(flask);
        return flask;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
        NonNullList<ItemStack> remaining_items = NonNullList.withSize(input.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < remaining_items.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(Registration.VOLT_CELL_ITEM.get())) {
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
        return Registration.REACTION_FLASK_MODIFY_RECIPE_SERIALIZER.get();
    }
}
