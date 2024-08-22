package com.hyperlynx.reactive.components;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Objects;

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

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WIDE_RANGE =
            COMPONENT_TYPES.register("wide_range",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Unit.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WORLD_PIERCER =
            COMPONENT_TYPES.register("world_piercer",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Unit.CODEC)
                            .build());

    public static boolean checkHasEnchant(ItemStack stack, Holder<DataComponentType<?>> enchantment_component){
        if(stack.has(DataComponents.ENCHANTMENTS)){
            for(var enchant : Objects.requireNonNull(stack.get(DataComponents.ENCHANTMENTS)).keySet()){
                if(enchant.value().effects().has(enchantment_component.value())){
                    return true;
                }
            }
        }
        return false;
    }
}
