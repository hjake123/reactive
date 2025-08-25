package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public record Formula(Map<Power, Integer> powers, Holder<Item> base_material) {
    public static final Codec<Formula> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Power.CODEC, Codec.INT).fieldOf("powers").forGetter(Formula::powers),
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("base_material").forGetter(Formula::base_material)
    ).apply(instance, Formula::new));

    public Formula copy() {
        return new Formula(new HashMap<>(powers), base_material);
    }
}