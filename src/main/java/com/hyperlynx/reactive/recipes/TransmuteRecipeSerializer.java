package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipeSerializer implements RecipeSerializer<TransmuteRecipe> {
    @Override
    @NotNull
    public TransmuteRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        ItemStack reactant = CraftingHelper.getItemStack(json.get("reactant").getAsJsonObject(), false);
        ItemStack product = CraftingHelper.getItemStack(json.get("product").getAsJsonObject(), false);
        List<Power> reagents = new ArrayList<>();
        for(JsonElement j : json.get("reagents").getAsJsonArray()){
            RegistryObject<Power> powObj = RegistryObject.create(ResourceLocation.tryParse(j.getAsString()), Registration.POWER_SUPPLIER.get());
            if(powObj.isPresent())
                reagents.add(powObj.get());
            else
                System.err.println("Tried to read a fake power " + j.getAsString() + " in recipe " + id);
        }
        int width = json.get("width").getAsInt();
        int cost = json.get("cost").getAsInt();
        return new TransmuteRecipe(id, "transmutation", reactant, product, reagents, width, cost);
    }

    @Override
    public @Nullable TransmuteRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        ItemStack reactant = buffer.readItem();
        ItemStack product = buffer.readItem();
        List<Power> reagents = new ArrayList<>();
        while(buffer.isReadable()){
            reagents.add(buffer.readRegistryId());
        }
        int width = buffer.readInt();
        int cost = buffer.readInt();
        return new TransmuteRecipe(id, "transmutation", reactant, product, reagents, width, cost);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull TransmuteRecipe recipe) {
        buffer.writeItem(recipe.reactant);
        buffer.writeItem(recipe.product);
        for(Power p : recipe.reagents){
            buffer.writeRegistryId(Registration.POWER_SUPPLIER.get(), p);
        }
        buffer.writeInt(recipe.window_width);
        buffer.writeInt(recipe.cost);
    }

}
