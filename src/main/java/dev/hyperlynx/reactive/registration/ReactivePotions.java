package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactivePotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, ReactiveMod.MODID);

    public static final DeferredHolder<Potion, Potion> LONG_NULL_GRAVITY = POTIONS.register("no_gravity_long",
            () -> new Potion("no_gravity_long", new MobEffectInstance(ReactiveMobEffects.NULL_GRAVITY, 8000)));

    public static final DeferredHolder<Potion, Potion> NULL_GRAVITY = POTIONS.register("no_gravity",
            () -> new Potion("no_gravity", new MobEffectInstance(ReactiveMobEffects.NULL_GRAVITY, 3000)));
}
