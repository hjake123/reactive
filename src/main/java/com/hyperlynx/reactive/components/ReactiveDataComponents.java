package com.hyperlynx.reactive.components;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveDataComponents {
    // Registration class for data component types.
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ReactiveMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WarpBottleTarget>> WARP_BOTTLE_TARGET_COMPONENT = COMPONENT_TYPES.register("warp_bottle_target",
            () -> DataComponentType.<WarpBottleTarget>builder()
                    .persistent(WarpBottleTarget.CODEC)
                    .networkSynchronized(WarpBottleTarget.STREAM_CODEC)
                    .build()
    );
}
