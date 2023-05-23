package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DissolveRecipeSerializer implements RecipeSerializer<DissolveRecipe> {
    @Override
    @NotNull
    public DissolveRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        try {
            Ingredient reactant = CraftingHelper.getIngredient(json.get("reactant").getAsJsonObject());
            ItemStack product = CraftingHelper.getItemStack(json.get("product").getAsJsonObject(), false);
            boolean needs_electricity = false;
            if(json.has("needs_electricity"))
                needs_electricity = json.get("needs_electricity").getAsBoolean();
            return new DissolveRecipe(id, "dissolve", reactant, product, needs_electricity);
        }catch(JsonSyntaxException e){
            return null;
        }
    }

    @Override
    public @Nullable DissolveRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        Ingredient reactant = Ingredient.fromNetwork(buffer);
        ItemStack product = buffer.readItem();
        boolean needs_electricity = buffer.readBoolean();
        return new DissolveRecipe(id, "dissolve", reactant, product, needs_electricity);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DissolveRecipe recipe) {
        recipe.getReactant().toNetwork(buffer);
        buffer.writeItem(recipe.product);
        buffer.writeBoolean(recipe.needs_electricity);
    }

}
