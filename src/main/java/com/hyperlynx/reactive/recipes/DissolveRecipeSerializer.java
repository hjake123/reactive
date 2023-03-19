package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DissolveRecipeSerializer implements RecipeSerializer<DissolveRecipe> {
    @Override
    @NotNull
    public DissolveRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        try {
            ItemStack reactant = CraftingHelper.getItemStack(json.get("reactant").getAsJsonObject(), false);
            ItemStack product = CraftingHelper.getItemStack(json.get("product").getAsJsonObject(), false);
            return new DissolveRecipe(id, "dissolve", reactant, product);
        }catch(JsonSyntaxException e){
            return null;
        }
    }

    @Override
    public @Nullable DissolveRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        ItemStack reactant = buffer.readItem();
        ItemStack product = buffer.readItem();
        return new DissolveRecipe(id, "dissolve", reactant, product);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DissolveRecipe recipe) {
        buffer.writeItem(recipe.reactant);
        buffer.writeItem(recipe.product);
    }

}
