package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

// Registers the Alchemical Powers.
@EventBusSubscriber(modid=ReactiveMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class Powers {
    // Handles registration of Powers.
    public static final ResourceKey<Registry<Power>> POWER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(ReactiveMod.MODID, "powers"));
    public static final Registry<Power> POWER_REGISTRY = new RegistryBuilder<>(POWER_REGISTRY_KEY)
            .sync(true)
            .defaultKey(new ResourceLocation(ReactiveMod.MODID, "nothing"))
            .create();
    public static final DeferredRegister<Power> POWERS = DeferredRegister.create(POWER_REGISTRY, ReactiveMod.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(POWER_REGISTRY);
    }

    public static final DeferredHolder<Power, Power> BLAZE_POWER = POWERS.register("blaze", () -> new Power("blaze", 0xFFA300, Registration.BLAZE_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> MIND_POWER = POWERS.register("mind", () -> new Power("mind", 0x7A5BB5, Registration.MIND_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> SOUL_POWER = POWERS.register("soul", () -> new Power("soul", 0x60F5FA, Registration.SOUL_BOTTLE.get()));
    public static final DeferredHolder<Power, Power>CURSE_POWER = POWERS.register("curse", () -> new Power("curse", 0x2D231D, (Item) null, Items.BLACK_DYE));
    public static final DeferredHolder<Power, Power> LIGHT_POWER = POWERS.register("light", () -> new Power("light", 0xF6DAB4, Registration.LIGHT_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> WARP_POWER = POWERS.register("warp", () -> new Power("warp", 0x118066, Registration.WARP_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> VITAL_POWER = POWERS.register("vital", () -> new Power("vital", 0xFF0606, Registration.VITAL_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> BODY_POWER = POWERS.register("body", () -> new Power("body", 0xAF5220, Registration.BODY_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> VERDANT_POWER = POWERS.register("verdant", () -> new Power("verdant", 0x3ADB00, Registration.VERDANT_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> ACID_POWER = POWERS.register("caustic", () -> new Power("caustic", 0x9D1E2D,  Registration.ACID_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> X_POWER = POWERS.register("esoteric_x", () -> new Power("esoteric_x", 0x9800FF, null, Items.MAGENTA_DYE));
    public static final DeferredHolder<Power, Power> Y_POWER = POWERS.register("esoteric_y", () -> new Power("esoteric_y", 0xADEA12,null, Items.LIME_DYE));
    public static final DeferredHolder<Power, Power> Z_POWER = POWERS.register("esoteric_z", () -> new Power("esoteric_z", 0xDACCE8, null, Items.GRAY_DYE));
    public static final DeferredHolder<Power, Power> ASTRAL_POWER = POWERS.register("astral", () -> new Power("astral", 0xE9D7FA, null, Items.WHITE_DYE));

}