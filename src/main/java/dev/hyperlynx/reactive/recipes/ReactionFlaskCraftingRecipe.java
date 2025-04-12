package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.components.ReactionFlaskContents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReactionFlaskCraftingRecipe extends CustomRecipe {
    public static TagKey<Item> POWER_BOTTLE_TAG = ItemTags.create(ReactiveMod.location("power_bottles"));

    public ReactionFlaskCraftingRecipe(CraftingBookCategory category) {
        super(category);
    }

    private static Map<Power, Integer> getPowerBalance(CraftingInput input){
        HashMap<Power, Integer> powers = new HashMap<>();
        for(ItemStack stack : input.items()){
            for(Power power : Powers.POWERS.getRegistry().get()){
                if(power.matchesBottle(stack)){
                    powers.put(power, WorldSpecificValues.BOTTLE_RETURN.get());
                }
            }
        }
        return powers;
    }

    // Warning! Row and Column is reversed from what one would reasonably expect. Thanks vanilla!

    private boolean matchOneWide(CraftingInput input) {
        if(!input.getItem(0, 0).is(Registration.INERT_CRYSTAL.get()) || !input.getItem(0, 2).is(Registration.GOLD_THREAD.get())){
            return false;
        }
        return input.getItem(1, 0).is(POWER_BOTTLE_TAG);
    }

    private boolean matchTwoWide(CraftingInput input) {
        if(
                !(input.getItem(0, 0).is(Registration.INERT_CRYSTAL.get()) && input.getItem(0, 2).is(Registration.GOLD_THREAD.get()))
                && !(input.getItem(1, 0).is(Registration.INERT_CRYSTAL.get()) && input.getItem(1, 2).is(Registration.GOLD_THREAD.get()))
        ){
            return false;
        }
        return input.getItem(0, 1).is(POWER_BOTTLE_TAG)
                && input.getItem(1, 1).is(POWER_BOTTLE_TAG);
    }

    private boolean matchThreeWide(CraftingInput input) {
        if(!input.getItem(1, 0).is(Registration.INERT_CRYSTAL.get()) || !input.getItem(1, 2).is(Registration.GOLD_THREAD.get())){
            return false;
        }
        return input.getItem(0, 1).is(POWER_BOTTLE_TAG)
                && input.getItem(1, 1).is(POWER_BOTTLE_TAG)
                && input.getItem(2, 1).is(POWER_BOTTLE_TAG);
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        if(input.height() == 3 && input.width() == 1){
            return matchOneWide(input);
        }
        if(input.height() == 3 && input.width() == 2){
            return matchTwoWide(input);
        }
        if(input.height() == 3 && input.width() == 3){
            return matchThreeWide(input);
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        var powers = getPowerBalance(input);
        ItemStack result = Registration.REACTION_FLASK.get().getDefaultInstance();
        result.set(Registration.REACTION_FLASK_CONTENTS.get(), new ReactionFlaskContents(powers, false));
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
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
        return width >= 3 && height >= 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Registration.REACTION_FLASK_RECIPE_SERIALIZER.get();
    }
}
