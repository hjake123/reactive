package dev.hyperlynx.reactive.registration;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.components.BoundEntity;
import dev.hyperlynx.reactive.components.LitmusMeasurement;
import dev.hyperlynx.reactive.components.ReactionFlaskContents;
import dev.hyperlynx.reactive.components.WarpBottleTarget;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ReactiveComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ReactiveMod.MODID);
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, ReactiveMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ReactionFlaskContents>> REACTION_FLASK_CONTENTS =
            COMPONENT_TYPES.register("reaction_flask_contents",
                    () -> DataComponentType.<ReactionFlaskContents>builder()
                            .persistent(ReactionFlaskContents.CODEC)
                            .networkSynchronized(ReactionFlaskContents.STREAM_CODEC)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpBottleTarget>> WARP_BOTTLE_TARGET =
            COMPONENT_TYPES.register("warp_bottle_target",
                    () -> DataComponentType.<WarpBottleTarget>builder()
                            .persistent(WarpBottleTarget.CODEC)
                            .networkSynchronized(WarpBottleTarget.STREAM_CODEC)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LitmusMeasurement>> LITMUS_MEASUREMENT =
            COMPONENT_TYPES.register("litmus_measurement",
                    () -> DataComponentType.<LitmusMeasurement>builder()
                            .persistent(LitmusMeasurement.CODEC)
                            .networkSynchronized(LitmusMeasurement.STREAM_CODEC)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BoundEntity>> BOUND_ENTITY =
            COMPONENT_TYPES.register("bound_entity",
                    () -> DataComponentType.<BoundEntity>builder()
                            .persistent(BoundEntity.CODEC)
                            .networkSynchronized(BoundEntity.STREAM_CODEC)
                            .build());


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> TUTORIAL_DONE =
            COMPONENT_TYPES.register("tutorial",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Codec.unit(Unit.INSTANCE))
                            .networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WORLD_PIERCER =
            ENCHANTMENT_COMPONENT_TYPES.register("world_piercer",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Unit.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> WIDE_RANGE =
            ENCHANTMENT_COMPONENT_TYPES.register("wide_range",
                    () -> DataComponentType.<Unit>builder()
                            .persistent(Unit.CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STAFF_RATE =
            ENCHANTMENT_COMPONENT_TYPES.register("staff_rate",
                    () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
                            .build());


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> STAFF_DAMAGE =
            ENCHANTMENT_COMPONENT_TYPES.register("staff_damage",
                    () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
                            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf())
                            .build());
}
