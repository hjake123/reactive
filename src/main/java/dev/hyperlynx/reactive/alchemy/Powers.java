package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.*;
import org.checkerframework.checker.units.qual.A;

import java.awt.image.PackedColorModel;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

// Registers the Alchemical Powers.
@EventBusSubscriber(modid=ReactiveMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class Powers {
    // Handles registration of Powers.
    public static final ResourceKey<Registry<Power>> POWER_REGISTRY_KEY = ResourceKey.createRegistryKey(ReactiveMod.location( "powers"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                // The registry key.
                POWER_REGISTRY_KEY,
                // The codec of the registry contents.
                Power.CODEC,
                // The network codec of the registry contents.
                Power.CODEC
        );
    }

    // These keys are used to create the built-in powers in datagen.
    public static final ResourceKey<Power> BLAZE_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("blaze"));
    public static final ResourceKey<Power> SOUL_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("soul"));
    public static final ResourceKey<Power> MIND_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("mind"));
    public static final ResourceKey<Power> LIGHT_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("light"));
    public static final ResourceKey<Power> WARP_KEY =  ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("warp"));
    public static final ResourceKey<Power> VITAL_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("vital"));
    public static final ResourceKey<Power> CURSE_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("curse"));
    public static final ResourceKey<Power> VERDANT_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("verdant"));
    public static final ResourceKey<Power> BODY_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("body"));
    public static final ResourceKey<Power> ACID_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("caustic"));
    public static final ResourceKey<Power> X_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("esoteric_x"));
    public static final ResourceKey<Power> Y_KEY =  ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("esoteric_y"));
    public static final ResourceKey<Power> Z_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("esoteric_z"));
    public static final ResourceKey<Power> FLOW_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("flow"));
    public static final ResourceKey<Power> OMEN_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("omen"));
    public static final ResourceKey<Power> ASTRAL_KEY = ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("astral"));


    // These holders are used to actually access the Powers by the mod.
    public static final DeferredHolder<Power, Power> BLAZE_POWER = DeferredHolder.create(BLAZE_KEY);
    public static final DeferredHolder<Power, Power> SOUL_POWER = DeferredHolder.create(SOUL_KEY);
    public static final DeferredHolder<Power, Power> MIND_POWER = DeferredHolder.create(MIND_KEY);
    public static final DeferredHolder<Power, Power> LIGHT_POWER = DeferredHolder.create(LIGHT_KEY);
    public static final DeferredHolder<Power, Power> WARP_POWER = DeferredHolder.create(WARP_KEY);
    public static final DeferredHolder<Power, Power> VITAL_POWER = DeferredHolder.create(VITAL_KEY);
    public static final DeferredHolder<Power, Power> CURSE_POWER = DeferredHolder.create(CURSE_KEY);
    public static final DeferredHolder<Power, Power> VERDANT_POWER = DeferredHolder.create(VERDANT_KEY);
    public static final DeferredHolder<Power, Power> BODY_POWER = DeferredHolder.create(BODY_KEY);
    public static final DeferredHolder<Power, Power> ACID_POWER = DeferredHolder.create(ACID_KEY);
    public static final DeferredHolder<Power, Power> X_POWER = DeferredHolder.create(X_KEY);
    public static final DeferredHolder<Power, Power> Y_POWER = DeferredHolder.create(Y_KEY);
    public static final DeferredHolder<Power, Power> Z_POWER = DeferredHolder.create(Z_KEY);
    public static final DeferredHolder<Power, Power> FLOW_POWER = DeferredHolder.create(FLOW_KEY);
    public static final DeferredHolder<Power, Power> OMEN_POWER = DeferredHolder.create(OMEN_KEY);
    public static final DeferredHolder<Power, Power> ASTRAL_POWER = DeferredHolder.create(ASTRAL_KEY);

    public static Registry<Power> getPowerRegistry(RegistryAccess access){
        Optional<Registry<Power>> possible_power_registry = access.registry(POWER_REGISTRY_KEY);
        if(possible_power_registry.isEmpty()){
            throw new RuntimeException("Couldn't load Power registry!");
        }
        return possible_power_registry.get();
    }

    public static Power get(ResourceKey<Power> key, RegistryAccess access){
        Registry<Power> power_registry = getPowerRegistry(access);

        if(!power_registry.containsKey(key)){
            ReactiveMod.LOGGER.error("Power {} did not exist in the registry!", key);
        }
        return power_registry.get(key);
    }

    public static Power get(ResourceLocation location, RegistryAccess access) {
        return get(ResourceKey.create(POWER_REGISTRY_KEY, location), access);
    }

    public static Holder.Reference<Power> holder(ResourceKey<Power> key, RegistryAccess access){
        return access.lookup(Powers.POWER_REGISTRY_KEY).get().get(key).get();
    }

    public static Stream<Power> stream(RegistryAccess access){
        return getPowerRegistry(access).stream();
    }

    public static List<Power> list(RegistryAccess access){
        return stream(access).toList();
    }
}