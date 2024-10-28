package dev.hyperlynx.reactive.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// Just a way to access the mob effect constructor.
public class HyperMobEffect extends MobEffect {
    public HyperMobEffect(MobEffectCategory cat, int color) {
        super(cat, color);
    }
}
