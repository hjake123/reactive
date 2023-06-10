package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeFriendlyByteBuf;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipeSerializer implements RecipeSerializer<TransmuteRecipe> {
    @Override
    @NotNull
    public TransmuteRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        try {
            Ingredient reactant = CraftingHelper.getIngredient(json.get("reactant").getAsJsonObject(), false);
            ItemStack product = CraftingHelper.getItemStack(json.get("product").getAsJsonObject(), false);
            List<Power> reagents = new ArrayList<>();
            for (JsonElement j : json.get("reagents").getAsJsonArray()) {
                RegistryObject<Power> powObj = RegistryObject.create(ResourceLocation.tryParse(j.getAsString()), Powers.POWER_SUPPLIER.get());
                if (powObj.isPresent())
                    reagents.add(powObj.get());
                else
                    System.err.println("Tried to read a fake power " + j.getAsString() + " in recipe " + id);
            }
            int min = json.get("min").getAsInt();
            int cost = json.get("cost").getAsInt();
            boolean needs_electricity = false;
            if(json.has("needs_electricity"))
                needs_electricity = json.get("needs_electricity").getAsBoolean();
            return new TransmuteRecipe(id, "transmutation", reactant, product, reagents, min, cost, needs_electricity);
        }
        catch(JsonSyntaxException e){
            return null;
        }
    }

    @Override
    public @Nullable TransmuteRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        Ingredient reactant = Ingredient.fromNetwork(buffer);
        ItemStack product = buffer.readItem();
        List<Power> reagents = buffer.readCollection(ArrayList::new, IForgeFriendlyByteBuf::readRegistryId);
        int min = buffer.readVarInt();
        int cost = buffer.readVarInt();
        boolean needs_electricity = buffer.readBoolean();
        return new TransmuteRecipe(id, "transmutation", reactant, product, reagents, min, cost, needs_electricity);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull TransmuteRecipe recipe) {
        recipe.reactant.toNetwork(buffer);
        buffer.writeItem(recipe.product);
        buffer.writeCollection(recipe.reagents, (FriendlyByteBuf b, Power p) -> b.writeRegistryId(Powers.POWER_SUPPLIER.get(), p));
        buffer.writeVarInt(recipe.minimum);
        buffer.writeVarInt(recipe.cost);
        buffer.writeBoolean(recipe.needs_electricity);
    }

}
