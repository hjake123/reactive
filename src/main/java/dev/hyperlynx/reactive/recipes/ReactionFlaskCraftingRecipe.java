package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.items.ReactionFlaskItem;
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
        return input.getItem(4).is(POWER_BOTTLE_TAG);
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

    @Override
    public boolean matches(CraftingContainer input, @NotNull Level level) {
        if(input.getHeight() == 3 && input.getWidth() == 1){
            return matchOneWide(input);
        }
        if(input.getHeight() == 3 && input.getWidth() == 2){
            return matchTwoWide(input);
        }
        if(input.getHeight() == 3 && input.getWidth() == 3){
            return matchThreeWide(input);
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer input, RegistryAccess pRegistryAccess) {
        var powers = getPowerBalance(input);
        ItemStack result = Registration.REACTION_FLASK_ITEM.get().getDefaultInstance();
        ReactionFlaskItem.Contents contents = new ReactionFlaskItem.Contents(powers, false);
        contents.saveToStack(result);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer input) {
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
