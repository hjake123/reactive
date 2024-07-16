package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hyperlynx.reactive.Registration;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DissolveRecipeSerializer implements RecipeSerializer<DissolveRecipe> {

    public static final Codec<DissolveRecipe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("group", "dissolve").forGetter(DissolveRecipe::getGroup),
                    Ingredient.CODEC_NONEMPTY.fieldOf("reactant").forGetter(DissolveRecipe::getReactant),
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("product").forGetter(DissolveRecipe::getProduct),
                    Codec.BOOL.optionalFieldOf("needs_electricity", false).forGetter(DissolveRecipe::isElectricityRequired)
            ).apply(instance, DissolveRecipe::new));

    @Override
    public Codec<DissolveRecipe> codec() {
        return CODEC;
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull DissolveRecipe recipe) {
        recipe.getReactant().toNetwork(buffer);
        buffer.writeItem(recipe.product);
        buffer.writeBoolean(recipe.needs_electricity);
    }

    @Override
    public @Nullable DissolveRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        Ingredient reactant = Ingredient.fromNetwork(buffer);
        ItemStack product = buffer.readItem();
        boolean needs_electricity = buffer.readBoolean();
        return new DissolveRecipe("dissolve", reactant, product, needs_electricity);
    }

}
