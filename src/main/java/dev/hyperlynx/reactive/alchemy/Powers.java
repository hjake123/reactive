package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.registration.ReactiveBlocks;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

// Registers the Alchemical Powers.
@EventBusSubscriber(modid=ReactiveMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class Powers {
    // Handles registration of Powers.
    public static final ResourceKey<Registry<Power>> POWER_REGISTRY_KEY = ResourceKey.createRegistryKey(ReactiveMod.location( "powers"));
    public static final Registry<Power> POWER_REGISTRY = new RegistryBuilder<>(POWER_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ReactiveMod.location( "nothing"))
            .create();
    public static final DeferredRegister<Power> POWERS = DeferredRegister.create(POWER_REGISTRY, ReactiveMod.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(POWER_REGISTRY);
    }

    public static final DeferredHolder<Power, Power> BLAZE_POWER = POWERS.register("blaze", () -> new Power("blaze", 0xFFA300, Blocks.WATER, ReactiveItems.BLAZE_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> MIND_POWER = POWERS.register("mind", () -> new Power("mind", 0x7A5BB5, ReactiveBlocks.DUMMY_MAGIC_WATER.get(), ReactiveItems.MIND_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> SOUL_POWER = POWERS.register("soul", () -> new Power("soul", 0x60F5FA, ReactiveBlocks.DUMMY_FAST_WATER.get(), ReactiveItems.SOUL_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> CURSE_POWER = POWERS.register("curse", () -> new Power("curse", 0x2D231D, ReactiveBlocks.DUMMY_NOISE_WATER.get(),null));
    public static final DeferredHolder<Power, Power> LIGHT_POWER = POWERS.register("light", () -> new Power("light", 0xF6DAB4, ReactiveBlocks.DUMMY_MAGIC_WATER.get(), ReactiveItems.LIGHT_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> WARP_POWER = POWERS.register("warp", () -> new Power("warp", 0x118066, ReactiveBlocks.DUMMY_NOISE_WATER.get(), ReactiveItems.WARP_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> VITAL_POWER = POWERS.register("vital", () -> new Power("vital", 0xFF0606, Blocks.WATER, ReactiveItems.VITAL_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> BODY_POWER = POWERS.register("body", () -> new Power("body", 0xAF5220, Blocks.WATER, ReactiveItems.BODY_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> VERDANT_POWER = POWERS.register("verdant", () -> new Power("verdant", 0x3ADB00, Blocks.WATER, ReactiveItems.VERDANT_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> ACID_POWER = POWERS.register("caustic", () -> new Power("caustic", 0x9D1E2D,  Blocks.WATER, ReactiveItems.ACID_BOTTLE.get()));
    public static final DeferredHolder<Power, Power> X_POWER = POWERS.register("esoteric_x", () -> new Power("esoteric_x", 0x9800FF, ReactiveBlocks.DUMMY_FAST_WATER.get(), null));
    public static final DeferredHolder<Power, Power> Y_POWER = POWERS.register("esoteric_y", () -> new Power("esoteric_y", 0xADEA12, ReactiveBlocks.DUMMY_MAGIC_WATER.get(),null));
    public static final DeferredHolder<Power, Power> Z_POWER = POWERS.register("esoteric_z", () -> new Power("esoteric_z", 0xDACCE8, ReactiveBlocks.DUMMY_NOISE_WATER.get(),null));
    public static final DeferredHolder<Power, Power> FLOW_POWER = POWERS.register("flow", () -> new Power("flow", 0x7A82C4, ReactiveBlocks.DUMMY_FAST_WATER.get(),null));
    public static final DeferredHolder<Power, Power> OMEN_POWER = POWERS.register("omen", () -> new Power("omen", 0x2A4455, ReactiveBlocks.DUMMY_SLOW_WATER.get(), Items.OMINOUS_BOTTLE));
    public static final DeferredHolder<Power, Power> ASTRAL_POWER = POWERS.register("astral", () -> new Power("astral", 0xE9D7FA, ReactiveBlocks.DUMMY_MAGIC_WATER.get(),null));

}