package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension;
import net.neoforged.neoforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrecipitateRecipeSerializer implements RecipeSerializer<PrecipitateRecipe> {
    @Override
    @NotNull
    public PrecipitateRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        ItemStack product = CraftingHelper.getItemStack(json.get("product").getAsJsonObject(), false);
        List<Power> reagents = new ArrayList<>();
        for(JsonElement j : json.get("reagents").getAsJsonArray()){
            RegistryObject<Power> powObj = RegistryObject.create(ResourceLocation.tryParse(j.getAsString()), Powers.POWER_SUPPLIER.get());
            if(powObj.isPresent())
                reagents.add(powObj.get());
            else
                System.err.println("Tried to read a fake power " + j.getAsString() + " in recipe " + id);
        }
        int min = json.get("min").getAsInt();
        int cost = json.get("cost").getAsInt();
        int reagent_count = json.get("reagent_count").getAsInt();
        boolean needs_electricity = false;
        if(json.has("needs_electricity"))
            needs_electricity = json.get("needs_electricity").getAsBoolean();
        return new PrecipitateRecipe(id, "precipitation", product, reagents, min, cost, reagent_count, needs_electricity);
    }

    @Override
    public @Nullable PrecipitateRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        ItemStack product = buffer.readItem();
        List<Power> reagents = buffer.readCollection(ArrayList::new, IFriendlyByteBufExtension::readRegistryId);
        int min = buffer.readInt();
        int cost = buffer.readInt();
        int reagent_count = buffer.readInt();
        boolean needs_electricity = buffer.readBoolean();
        return new PrecipitateRecipe(id, "precipitation", product, reagents, min, cost, reagent_count, needs_electricity);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull PrecipitateRecipe recipe) {
        buffer.writeItem(recipe.product);
        buffer.writeCollection(recipe.reagents, (FriendlyByteBuf b, Power p) -> b.writeRegistryId(Powers.POWER_SUPPLIER.get(), p));
        buffer.writeInt(recipe.minimum);
        buffer.writeInt(recipe.cost);
        buffer.writeInt(recipe.reagent_count);
        buffer.writeBoolean(recipe.needs_electricity);
    }

}
