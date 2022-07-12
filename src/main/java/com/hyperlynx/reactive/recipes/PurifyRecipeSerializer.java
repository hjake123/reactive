package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PurifyRecipeSerializer implements RecipeSerializer<PurifyRecipe> {
    @Override
    @NotNull
    public PurifyRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        ItemStack reactant = CraftingHelper.getItemStack(json.get("reactant").getAsJsonObject(), false);
        ItemStack product = CraftingHelper.getItemStack(json.get("product").getAsJsonObject(), false);
        return new PurifyRecipe(id, "purification", reactant, product);
    }

    @Override
    public @Nullable PurifyRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        ItemStack reactant = buffer.readItem();
        ItemStack product = buffer.readItem();
        return new PurifyRecipe(id, "purification", reactant, product);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull PurifyRecipe recipe) {
        buffer.writeItem(recipe.reactant);
        buffer.writeItem(recipe.product);
    }

}
