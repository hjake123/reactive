package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ReactiveMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> RUMBLE = SOUND_EVENTS.register("rumble",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.parse("reactive:rumble")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ZAP = SOUND_EVENTS.register("zap",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.parse("reactive:zap")));
}
