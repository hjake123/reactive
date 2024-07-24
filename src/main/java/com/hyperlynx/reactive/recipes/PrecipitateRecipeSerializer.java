package com.hyperlynx.reactive.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrecipitateRecipeSerializer implements RecipeSerializer<PrecipitateRecipe> {

    public static final Codec<PrecipitateRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "transmute").forGetter(PrecipitateRecipe::getGroup),
            ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("product").forGetter(PrecipitateRecipe::getProduct),
            Powers.POWERS.getRegistry().get().byNameCodec().listOf().fieldOf("reagents").forGetter(PrecipitateRecipe::getReagents),
            Codec.INT.fieldOf("min").forGetter(PrecipitateRecipe::getMinimum),
            Codec.INT.fieldOf("cost").forGetter(PrecipitateRecipe::getCost),
            Codec.INT.fieldOf("reagent_count").forGetter(PrecipitateRecipe::getReagentCount),
            Codec.BOOL.optionalFieldOf("needs_electricity", false).forGetter(PrecipitateRecipe::isElectricityRequired)
    ).apply(instance, PrecipitateRecipe::new));

    @Override
    public Codec<PrecipitateRecipe> codec() {
        return CODEC;
    }

    @Override
    public @Nullable PrecipitateRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        ItemStack product = buffer.readItem();
        List<ResourceLocation> reagent_locations = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readResourceLocation);
        List<Power> reagents = new ArrayList<>();
        for(var location : reagent_locations){
            reagents.add(Powers.POWERS.getRegistry().get().get(location));
        }
        int min = buffer.readInt();
        int cost = buffer.readInt();
        int reagent_count = buffer.readInt();
        boolean needs_electricity = buffer.readBoolean();
        return new PrecipitateRecipe("precipitation", product, reagents, min, cost, reagent_count, needs_electricity);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull PrecipitateRecipe recipe) {
        buffer.writeItem(recipe.product);
        buffer.writeCollection(recipe.reagents, (FriendlyByteBuf b, Power p) -> b.writeResourceLocation(p.getResourceLocation()));
        buffer.writeInt(recipe.minimum);
        buffer.writeInt(recipe.cost);
        buffer.writeInt(recipe.reagent_count);
        buffer.writeBoolean(recipe.needs_electricity);
    }

}
