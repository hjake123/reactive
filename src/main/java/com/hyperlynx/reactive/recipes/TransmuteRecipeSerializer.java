package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeFriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<TransmuteRecipe> {
    ResourceLocation name;
    @Override
    @NotNull
    public TransmuteRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        try {
            ItemStack reactant = CraftingHelper.getItemStack(json.get("reactant").getAsJsonObject(), false);
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
            return new TransmuteRecipe(id, "transmutation", reactant, product, reagents, min, cost);
        }
        catch(JsonSyntaxException e){
            return TransmuteRecipe.EMPTY;
        }
    }

    @Override
    public @Nullable TransmuteRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        ItemStack reactant = buffer.readItem();
        ItemStack product = buffer.readItem();
        List<Power> reagents = buffer.readCollection(ArrayList::new, IForgeFriendlyByteBuf::readRegistryId);
        int min = buffer.readVarInt();
        int cost = buffer.readVarInt();
        return new TransmuteRecipe(id, "transmutation", reactant, product, reagents, min, cost);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull TransmuteRecipe recipe) {
        buffer.writeItem(recipe.reactant);
        buffer.writeItem(recipe.product);
        buffer.writeCollection(recipe.reagents, IForgeFriendlyByteBuf::writeRegistryId);
        buffer.writeVarInt(recipe.minimum);
        buffer.writeVarInt(recipe.cost);
    }
}
