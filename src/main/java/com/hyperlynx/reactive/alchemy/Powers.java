package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

// Registers the Alchemical Powers.
public class Powers {
    // Handles registration of Powers.
    public static final DeferredRegister<Power> POWERS = DeferredRegister.create(new ResourceLocation(ReactiveMod.MODID, "power_registry"), ReactiveMod.MODID);
    public static final Supplier<IForgeRegistry<Power>> POWER_SUPPLIER = POWERS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Power> BLAZE_POWER = POWERS.register("blaze", () -> new Power("blaze", 0xFFA300));
    public static final RegistryObject<Power> MIND_POWER = POWERS.register("mind", () -> new Power("mind", 0x7A5BB5));
    public static final RegistryObject<Power> SOUL_POWER = POWERS.register("soul", () -> new Power("soul", 0x60F5FA));
    public static final RegistryObject<Power> CURSE_POWER = POWERS.register("curse", () -> new Power("curse", 0x2D231D));
    public static final RegistryObject<Power> LIGHT_POWER = POWERS.register("light", () -> new Power("light", 0xF6DAB4));
    public static final RegistryObject<Power> WARP_POWER = POWERS.register("warp", () -> new Power("warp", 0x118066));
    public static final RegistryObject<Power> VITAL_POWER = POWERS.register("vital", () -> new Power("vital", 0xFF0606));
    public static final RegistryObject<Power> BODY_POWER = POWERS.register("body", () -> new Power("body", 0xAF5220));
    public static final RegistryObject<Power> VERDANT_POWER = POWERS.register("verdant", () -> new Power("verdant", 0x3ADB00));
    public static final RegistryObject<Power> ACID_POWER = POWERS.register("caustic", () -> new Power("caustic", 0x9D1E2D));

    // These 'esoteric Powers' are formed from rare reactions and don't have an associated item tag.
    public static final RegistryObject<Power> X_POWER = POWERS.register("esoteric_x", () -> new Power("esoteric_x", 0x9800FF));
    public static final RegistryObject<Power> Y_POWER = POWERS.register("esoteric_y", () -> new Power("esoteric_y", 0xADEA12));
    public static final RegistryObject<Power> Z_POWER = POWERS.register("esoteric_z", () -> new Power("esoteric_z", 0xDACCE8));
}