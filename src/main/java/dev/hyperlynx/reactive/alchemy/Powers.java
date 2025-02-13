package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import net.minecraft.client.Minecraft;
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

import java.awt.image.PackedColorModel;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
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

    // These keys are used to access the built-in Powers using the below get() method
    // The powers themselves are defined with data and data-genned with datagen.BuiltInPowerGenerator
    public static final PowerDefinition BLAZE_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("blaze")));
    public static final PowerDefinition SOUL_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("soul")));
    public static final PowerDefinition MIND_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("mind")));
    public static final PowerDefinition LIGHT_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("light")));
    public static final PowerDefinition WARP_POWER = new PowerDefinition( ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("warp")));
    public static final PowerDefinition VITAL_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("vital")));
    public static final PowerDefinition CURSE_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("curse")));
    public static final PowerDefinition VERDANT_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("verdant")));
    public static final PowerDefinition BODY_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("body")));
    public static final PowerDefinition ACID_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("caustic")));
    public static final PowerDefinition X_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("esoteric_x")));
    public static final PowerDefinition Y_POWER = new PowerDefinition( ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("esoteric_y")));
    public static final PowerDefinition Z_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("esoteric_z")));
    public static final PowerDefinition FLOW_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("flow")));
    public static final PowerDefinition OMEN_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("omen")));
    public static final PowerDefinition ASTRAL_POWER = new PowerDefinition(ResourceKey.create(POWER_REGISTRY_KEY, ReactiveMod.location("astral")));

    public static Registry<Power> getPowerRegistry(RegistryAccess access) {
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

    public static Power getOnClient(ResourceKey<Power> key) {
        return get(key, Minecraft.getInstance().getConnection().registryAccess());
    }

    public static Power get(ResourceLocation location, RegistryAccess access) {
        return get(ResourceKey.create(POWER_REGISTRY_KEY, location), access);
    }

    public static Power getOnClient(ResourceLocation location) {
        return get(location, Minecraft.getInstance().getConnection().registryAccess());
    }

    public static Stream<Power> stream(RegistryAccess access){
        return getPowerRegistry(access).stream();
    }

    public static List<Power> list(RegistryAccess access){
        return stream(access).toList();
    }

    // A definition for one of the built-in Powers, made for ease of use.
    public record PowerDefinition(ResourceKey<Power> key) {
        public Power get(RegistryAccess access) {
            return Powers.getPowerRegistry(access).get(key);
        }

        public Power get(PowerBearer bearer) {
            return Powers.getPowerRegistry(bearer.access()).get(key);
        }

        public Power get(Level level) {
            return Powers.getPowerRegistry(level.registryAccess()).get(key);
        }

        public Power getOnClient() {
            return Powers.getPowerRegistry(Minecraft.getInstance().getConnection().registryAccess()).get(key);
        }
    }
}