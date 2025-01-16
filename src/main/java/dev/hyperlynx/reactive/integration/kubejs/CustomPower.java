package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class CustomPower extends Power {
    MutableComponent custom_component = null;
    Supplier<Item> bottle_supplier;

    public CustomPower(ResourceLocation id, int color, Supplier<Block> render_water_block, Supplier<Item> bottle) {
        super(id, color, render_water_block, null);
        bottle_supplier = bottle;
    }

    @Override
    public String getName() {
        if(custom_component != null){
            return custom_component.getString();
        }
        return super.getName();
    }

    @Override
    public ItemStack getBottle() {
        this.bottle = bottle_supplier.get();
        return super.getBottle();
    }

    @Override
    public boolean hasBottle() {
        return bottle_supplier.get() != null;
    }
}
