package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

    public static final MapCodec<TransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "transmute").forGetter(TransmuteRecipe::getGroup),
            Ingredient.CODEC_NONEMPTY.fieldOf("reactant").forGetter(TransmuteRecipe::getReactant),
            ItemStack.CODEC.fieldOf("product").forGetter(TransmuteRecipe::getProduct),
            Powers.POWERS.getRegistry().get().byNameCodec().listOf().fieldOf("reagents").forGetter(TransmuteRecipe::getReagents),
            Codec.INT.fieldOf("min").forGetter(TransmuteRecipe::getMinimum),
            Codec.INT.fieldOf("cost").forGetter(TransmuteRecipe::getCost),
            Codec.BOOL.optionalFieldOf("needs_electricity", false).forGetter(TransmuteRecipe::isElectricityRequired)
    ).apply(instance, TransmuteRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> STREAM_CODEC = StreamCodec.of(TransmuteRecipeSerializer::toNetwork, TransmuteRecipeSerializer::fromNetwork);

    @Override
    public MapCodec<TransmuteRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public static @Nullable TransmuteRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buffer) {
        Ingredient reactant = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        ItemStack product = ItemStack.STREAM_CODEC.decode(buffer);
        List<ResourceLocation> reagent_locations = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readResourceLocation);
        List<Power> reagents = new ArrayList<>();
        for(var location : reagent_locations){
            reagents.add(Powers.POWERS.getRegistry().get().get(location));
        }        int min = buffer.readVarInt();
        int cost = buffer.readVarInt();
        boolean needs_electricity = buffer.readBoolean();
        return new TransmuteRecipe("transmutation", reactant, product, reagents, min, cost, needs_electricity);
    }

    public static void toNetwork(@NotNull RegistryFriendlyByteBuf buffer, @NotNull TransmuteRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.reactant);
        ItemStack.STREAM_CODEC.encode(buffer, recipe.product);
        buffer.writeCollection(recipe.reagents, (FriendlyByteBuf b, Power p) -> b.writeResourceLocation(p.getResourceLocation()));
        buffer.writeVarInt(recipe.minimum);
        buffer.writeVarInt(recipe.cost);
        buffer.writeBoolean(recipe.needs_electricity);
    }

}
