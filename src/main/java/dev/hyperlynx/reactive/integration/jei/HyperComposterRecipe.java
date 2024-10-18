package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ReactiveMod;
import mezz.jei.api.recipe.vanilla.IJeiCompostingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class HyperComposterRecipe implements IJeiCompostingRecipe {
    Holder<Item> holder;
    public HyperComposterRecipe(Holder<Item> holder){
        this.holder = holder;
    }

    @Override
    public @Unmodifiable List<ItemStack> getInputs() {
        return List.of(holder.value().getDefaultInstance());
    }

    @Override
    public float getChance() {
        var data = holder.getData(NeoForgeDataMaps.COMPOSTABLES);
        if(data == null){
            return -1.0F;
        }
        return data.chance();
    }

    @Override
    public ResourceLocation getUid() {
        return ReactiveMod.location("compost_"+ holder.value().getDescriptionId());
    }
}
