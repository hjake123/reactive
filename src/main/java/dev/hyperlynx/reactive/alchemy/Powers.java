package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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

    private static final HolderOwner<Power> POWER_OWNER = new HolderOwner<>() {
        @Override
        public boolean canSerializeIn(@NotNull HolderOwner<Power> owner) {
            return HolderOwner.super.canSerializeIn(owner);
        }
    };

    // These holders are used to actually access the Powers by the mod.
    public static final PowerHolder BLAZE_POWER = new PowerHolder(BLAZE_KEY);
    public static final PowerHolder SOUL_POWER = new PowerHolder(SOUL_KEY);
    public static final PowerHolder MIND_POWER = new PowerHolder(MIND_KEY);
    public static final PowerHolder LIGHT_POWER = new PowerHolder(LIGHT_KEY);
    public static final PowerHolder WARP_POWER = new PowerHolder(WARP_KEY);
    public static final PowerHolder VITAL_POWER = new PowerHolder(VITAL_KEY);
    public static final PowerHolder CURSE_POWER = new PowerHolder(CURSE_KEY);
    public static final PowerHolder VERDANT_POWER = new PowerHolder(VERDANT_KEY);
    public static final PowerHolder BODY_POWER = new PowerHolder(BODY_KEY);
    public static final PowerHolder ACID_POWER = new PowerHolder(ACID_KEY);
    public static final PowerHolder X_POWER = new PowerHolder(X_KEY);
    public static final PowerHolder Y_POWER = new PowerHolder(Y_KEY);
    public static final PowerHolder Z_POWER = new PowerHolder(Z_KEY);
    public static final PowerHolder FLOW_POWER = new PowerHolder(FLOW_KEY);
    public static final PowerHolder OMEN_POWER = new PowerHolder(OMEN_KEY);
    public static final PowerHolder ASTRAL_POWER = new PowerHolder(ASTRAL_KEY);

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

    // We know JSON is loaded by the time the world loads, and that nothing needs these power holders before then,
    // so binding them at level load makes sense.
    // TODO: Test /reload!
    public static void bindOnLoad(LevelEvent.Load event){
        PowerHolder.bindAllInstances(event.getLevel().registryAccess());
    }

    public static class PowerHolder extends Holder.Reference<Power> {
        private static final List<PowerHolder> INSTANCE_LIST = new ArrayList<>();

        protected PowerHolder(@Nullable ResourceKey<Power> key) {
            super(Type.STAND_ALONE, POWER_OWNER, key, null);
            INSTANCE_LIST.add(this);
        }

        public Power get(){
            return this.value();
        }

        // Fine, I'll defer the holder myself!
        public static void bindAllInstances(RegistryAccess access) {
            for(PowerHolder holder : INSTANCE_LIST){
                holder.bindValue(Powers.get(holder.key(), access));
            }
        }
    }

}