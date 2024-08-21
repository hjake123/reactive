package com.hyperlynx.reactive.components;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ReactiveEnchantmentComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, ReactiveMod.MODID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STAFF_DAMAGE =
            COMPONENT_TYPES.register("staff_damage",
                    () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STAFF_RATE =
            COMPONENT_TYPES.register("staff_rate",
                    () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> SUPER_MISSILE =
            COMPONENT_TYPES.register("super_missile",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Unit.CODEC)
                            .build());
}
