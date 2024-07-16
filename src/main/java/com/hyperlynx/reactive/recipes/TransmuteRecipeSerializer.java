package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransmuteRecipeSerializer implements RecipeSerializer<TransmuteRecipe> {

    public static final Codec<TransmuteRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "transmute").forGetter(TransmuteRecipe::getGroup),
            Ingredient.CODEC_NONEMPTY.fieldOf("reactant").forGetter(TransmuteRecipe::getReactant),
            ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("product").forGetter(TransmuteRecipe::getProduct),
            Powers.POWER_REGISTRY.get().byNameCodec().listOf().fieldOf("reagents").forGetter(TransmuteRecipe::getReagents),
            Codec.INT.fieldOf("min").forGetter(TransmuteRecipe::getMinimum),
            Codec.INT.fieldOf("cost").forGetter(TransmuteRecipe::getCost),
            Codec.BOOL.optionalFieldOf("needs_electricity", false).forGetter(TransmuteRecipe::isElectricityRequired)
    ).apply(instance, TransmuteRecipe::new));

    @Override
    public Codec<TransmuteRecipe> codec() {
        return CODEC;
    }

    @Override
    public @Nullable TransmuteRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        Ingredient reactant = Ingredient.fromNetwork(buffer);
        ItemStack product = buffer.readItem();
        List<Power> reagents = buffer.readCollection(ArrayList::new, IFriendlyByteBufExtension::readRegistryId);
        int min = buffer.readVarInt();
        int cost = buffer.readVarInt();
        boolean needs_electricity = buffer.readBoolean();
        return new TransmuteRecipe("transmutation", reactant, product, reagents, min, cost, needs_electricity);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull TransmuteRecipe recipe) {
        recipe.reactant.toNetwork(buffer);
        buffer.writeItem(recipe.product);
        buffer.writeCollection(recipe.reagents, (FriendlyByteBuf b, Power p) -> b.writeRegistryId(Powers.POWER_REGISTRY.get(), p));
        buffer.writeVarInt(recipe.minimum);
        buffer.writeVarInt(recipe.cost);
        buffer.writeBoolean(recipe.needs_electricity);
    }

}
