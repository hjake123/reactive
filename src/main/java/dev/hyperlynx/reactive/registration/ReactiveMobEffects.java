package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.util.HyperMobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ReactiveMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, ReactiveMod.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> HIGH_STEP = MOB_EFFECTS.register("high_step",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0x18AD88)
                    .addAttributeModifier(Attributes.STEP_HEIGHT, ReactiveMod.location("high_step_effect"),
                            1, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, HyperMobEffect> FIRE_SHIELD = MOB_EFFECTS.register("fire_shield",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0xFFA511));

    public static final DeferredHolder<MobEffect, MobEffect> FAR_REACH = MOB_EFFECTS.register("far_reach",
            () -> new HyperMobEffect(MobEffectCategory.BENEFICIAL, 0x7A5BB5)
                    .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, ReactiveMod.location("far_block_reach_effect"),
                            2, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, ReactiveMod.location("far_entity_reach_effect"),
                            2, AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<MobEffect, MobEffect> IMMOBILE = MOB_EFFECTS.register("immobility",
            () -> new HyperMobEffect(MobEffectCategory.NEUTRAL, 0x118066)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, ReactiveMod.location("immobility_slowness_effect"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.FLYING_SPEED, ReactiveMod.location("immobility_flying_slowness_effect"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredHolder<MobEffect, MobEffect> NULL_GRAVITY = MOB_EFFECTS.register("no_gravity",
            () -> new HyperMobEffect(MobEffectCategory.NEUTRAL, 0xC0BF77)
                    .addAttributeModifier(Attributes.GRAVITY, ReactiveMod.location("no_gravity_effect"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
}
