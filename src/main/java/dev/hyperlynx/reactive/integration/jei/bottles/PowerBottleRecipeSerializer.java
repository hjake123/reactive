package dev.hyperlynx.reactive.integration.jei.bottles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.recipes.DissolveRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PowerBottleRecipeSerializer implements RecipeSerializer<PowerBottleRecipe> {

    @Override
    @NotNull
    public PowerBottleRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
        try {
            JsonElement power_element = json.get("power");
            RegistryObject<Power> powObj = RegistryObject.create(ResourceLocation.tryParse(power_element.getAsString()), Powers.POWER_SUPPLIER.get());
            return new PowerBottleRecipe(id, "power_bottle", powObj.get());
        }catch(JsonSyntaxException e){
            return null;
        }
    }

    @Override
    public @Nullable PowerBottleRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
        Power power = buffer.readRegistryId();
        return new PowerBottleRecipe(id, "power_bottle", power);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull PowerBottleRecipe recipe) {
        buffer.writeRegistryId(Powers.POWER_SUPPLIER.get(), recipe.power);
    }
}
