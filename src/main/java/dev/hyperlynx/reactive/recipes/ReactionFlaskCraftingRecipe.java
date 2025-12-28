package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.items.ReactionFlaskItem;
import dev.hyperlynx.reactive.util.VirtualCraftingContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ReactionFlaskCraftingRecipe extends CustomRecipe {
    public static final TagKey<Item> POWER_BOTTLE_TAG = ItemTags.create(ReactiveMod.location("power_bottles"));

    public ReactionFlaskCraftingRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private static Map<Power, Integer> getPowerBalance(CraftingContainer input){
        HashMap<Power, Integer> powers = new HashMap<>();
        for(ItemStack stack : input.getItems()){
            for(Power power : Powers.POWER_SUPPLIER.get().getValues()){
                if(power.matchesBottle(stack)){
                    powers.put(power, WorldSpecificValues.BOTTLE_RETURN.get());
                }
            }
        }
        return powers;
    }

    // Warning! Row and Column is reversed from what one would reasonably expect. Thanks vanilla!

    private boolean matchOneWide(CraftingContainer input) {
        if(!input.getItem(0).is(Registration.INERT_CRYSTAL.get()) || !input.getItem(6).is(Registration.GOLD_THREAD.get())){
            return false;
        }
        return input.getItem(3).is(POWER_BOTTLE_TAG);
    }

    private boolean matchTwoWide(CraftingContainer input) {
        if(
                !(input.getItem(0).is(Registration.INERT_CRYSTAL.get()) && input.getItem(6).is(Registration.GOLD_THREAD.get()))
                && !(input.getItem(1).is(Registration.INERT_CRYSTAL.get()) && input.getItem(7).is(Registration.GOLD_THREAD.get()))
        ){
            return false;
        }
        return input.getItem(3).is(POWER_BOTTLE_TAG)
                && input.getItem(4).is(POWER_BOTTLE_TAG);
    }

    private boolean matchThreeWide(CraftingContainer input) {
        if(!input.getItem(1).is(Registration.INERT_CRYSTAL.get()) || !input.getItem(7).is(Registration.GOLD_THREAD.get())){
            return false;
        }
        return input.getItem(3).is(POWER_BOTTLE_TAG)
                && input.getItem(4).is(POWER_BOTTLE_TAG)
                && input.getItem(5).is(POWER_BOTTLE_TAG);
    }

    private int calculateWidth(CraftingContainer input) {
        int width = 0;
        if(!input.getItem(0).isEmpty() || !input.getItem(3).isEmpty() || !input.getItem(6).isEmpty()) {
            // Left column is populated.
            width++;
        }
        if(!input.getItem(1).isEmpty() || !input.getItem(4).isEmpty() || !input.getItem(7).isEmpty()) {
            // Center is populated.
            width++;
        }
        if(!input.getItem(2).isEmpty() || !input.getItem(5).isEmpty() || !input.getItem(8).isEmpty()) {
            // Right column is populated.
            width++;
        }
        return width;
    }

    private CraftingContainer leftAdjustAndCopy(CraftingContainer input) {
        VirtualCraftingContainer copy = new VirtualCraftingContainer(input);
        if(copy.all_empty) {
            return copy;
        }
        boolean left_column_is_empty = copy.getItem(0).isEmpty() && copy.getItem(3).isEmpty() && copy.getItem(6).isEmpty();
        while(left_column_is_empty) {
            // Shift all items to the left by one position and then check if the left column is still empty
            // Copy center column to left
            copy.setItem(0, copy.getItem(1));
            copy.setItem(3, copy.getItem(4));
            copy.setItem(6, copy.getItem(7));

            // Copy right column to center
            copy.setItem(1, copy.getItem(2));
            copy.setItem(4, copy.getItem(5));
            copy.setItem(7, copy.getItem(8));

            // Delete right column
            copy.setItem(2, ItemStack.EMPTY);
            copy.setItem(5, ItemStack.EMPTY);
            copy.setItem(8, ItemStack.EMPTY);

            // Recheck left column
            left_column_is_empty = copy.getItem(0).isEmpty() && copy.getItem(3).isEmpty() && copy.getItem(6).isEmpty();
        }
        return copy;
    }

    @Override
    public boolean matches(@NotNull CraftingContainer input, @NotNull Level level) {
        if (!canCraftInDimensions(input.getWidth(), input.getHeight())) {
            // Apparently Vanilla doesn't check this...? See issue #115
            return false;
        }
        int width = calculateWidth(input);
        CraftingContainer adjusted = leftAdjustAndCopy(input);
        if(width == 1){
            return matchOneWide(adjusted);
        }
        if(width == 2){
            return matchTwoWide(adjusted);
        }
        if(width == 3){
            return matchThreeWide(adjusted);
        }
        // ...how?
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer input, @NotNull RegistryAccess pRegistryAccess) {
        var powers = getPowerBalance(input);
        ItemStack result = Registration.REACTION_FLASK_ITEM.get().getDefaultInstance();
        ReactionFlaskItem.Contents contents = new ReactionFlaskItem.Contents(powers, false);
        contents.saveToStack(result);
        return result;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer input) {
        boolean already_removed_one_bottle = false;
        NonNullList<ItemStack> filtered_remaining_items = NonNullList.create();
        for(ItemStack stack : super.getRemainingItems(input)) {
            if(!already_removed_one_bottle && stack.is(Registration.QUARTZ_BOTTLE.get())) {
                already_removed_one_bottle = true;
                filtered_remaining_items.add(ItemStack.EMPTY);
            } else {
                filtered_remaining_items.add(stack);
            }
        }
        return filtered_remaining_items;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Registration.REACTION_FLASK_RECIPE_SERIALIZER.get();
    }
}
