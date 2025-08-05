package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.entites.data.ReactorData;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ReactiveEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, ReactiveMod.MODID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<ReactorData>> REACTOR_DATA_SERIALIZER =
            ENTITY_DATA_SERIALIZERS.register("reactor_data", ReactorData.Serializer::new);
}
