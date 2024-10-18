package dev.hyperlynx.reactive.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DissolveRecipeSerializer implements RecipeSerializer<DissolveRecipe> {

    public static final MapCodec<DissolveRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.STRING.optionalFieldOf("group", "dissolve").forGetter(DissolveRecipe::getGroup),
                    Ingredient.CODEC_NONEMPTY.fieldOf("reactant").forGetter(DissolveRecipe::getReactant),
                    ItemStack.STRICT_CODEC.fieldOf("product").forGetter(DissolveRecipe::getProduct),
                    Codec.BOOL.optionalFieldOf("needs_electricity", false).forGetter(DissolveRecipe::isElectricityRequired)
            ).apply(instance, DissolveRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DissolveRecipe> STREAM_CODEC = StreamCodec.of(DissolveRecipeSerializer::toNetwork, DissolveRecipeSerializer::fromNetwork);

    @Override
    public MapCodec<DissolveRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DissolveRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    public static void toNetwork(@NotNull RegistryFriendlyByteBuf buffer, @NotNull DissolveRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.reactant);
        ItemStack.STREAM_CODEC.encode(buffer, recipe.product);
        buffer.writeBoolean(recipe.needs_electricity);
    }

    public static @Nullable DissolveRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buffer) {
        var reactant = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        var product = ItemStack.STREAM_CODEC.decode(buffer);
        boolean needs_electricity = buffer.readBoolean();
        return new DissolveRecipe("dissolve", reactant, product, needs_electricity);
    }

}
