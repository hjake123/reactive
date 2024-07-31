package com.hyperlynx.reactive.components;

import com.hyperlynx.reactive.ReactiveMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveDataComponents {
    // Registration class for data component types.
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ReactiveMod.MODID);

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
}
