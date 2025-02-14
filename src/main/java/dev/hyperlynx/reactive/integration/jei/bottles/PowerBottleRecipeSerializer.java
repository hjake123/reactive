package dev.hyperlynx.reactive.integration.jei.bottles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PowerBottleRecipeSerializer implements RecipeSerializer<PowerBottleRecipe> {
    public static final MapCodec<PowerBottleRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "power_bottle").forGetter(PowerBottleRecipe::getGroup),
                Power.RESOURCE_KEY_CODEC.fieldOf("power").forGetter(PowerBottleRecipe::getPowerKey)
            ).apply(instance, PowerBottleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PowerBottleRecipe> STREAM_CODEC = StreamCodec.of(PowerBottleRecipeSerializer::toNetwork, PowerBottleRecipeSerializer::fromNetwork);

    public static @NotNull PowerBottleRecipe fromNetwork(@NotNull RegistryFriendlyByteBuf buffer) {
        return new PowerBottleRecipe("power_bottle", buffer.readResourceKey(Powers.POWER_REGISTRY_KEY));
    }

    public static void toNetwork(@NotNull RegistryFriendlyByteBuf buffer, @NotNull PowerBottleRecipe recipe) {
        buffer.writeResourceKey(recipe.power_key);
    }

    @Override
    public MapCodec<PowerBottleRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PowerBottleRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
