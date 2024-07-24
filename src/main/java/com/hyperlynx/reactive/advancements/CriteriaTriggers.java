package com.hyperlynx.reactive.advancements;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, ReactiveMod.MODID);

    //Register advancement criteria for the book
    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> MAKE_CRUCIBLE_TRIGGER = TRIGGERS.register("make_crucible_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "make_crucible_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> ENDER_PEARL_DISSOLVE_TRIGGER = TRIGGERS.register("dissolve_tp_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "dissolve_tp_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_SYNTHESIS_TRIGGER = TRIGGERS.register("see_synthesis_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_synthesis_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_CURSED_TRIGGER = TRIGGERS.register("be_cursed_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_cursed_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> TRY_NETHER_CRUCIBLE_TRIGGER = TRIGGERS.register("try_nether_crucible_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "try_nether_crucible_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> TRY_LAVA_CRUCIBLE_TRIGGER = TRIGGERS.register("try_lava_crucible_criterion",
            () -> new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "try_lava_crucible_criterion"), new ResourceLocation(ReactiveMod.MODID, "try_nether_crucible")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_SACRIFICE_TRIGGER = TRIGGERS.register("see_sacrifice_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_sacrifice_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> MAKE_RIFT_TRIGGER = TRIGGERS.register("make_rift_criterion",
            () -> new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "make_rift_criterion"), new ResourceLocation(ReactiveMod.MODID, "dissolve_tp")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> PORTAL_TRADE_TRIGGER = TRIGGERS.register("portal_trade_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "portal_trade_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> PORTAL_FREEZE_TRIGGER = TRIGGERS.register("portal_freeze_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "portal_freeze_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> PLACE_OCCULT_TRIGGER = TRIGGERS.register("place_eye_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "place_eye_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> OCCULT_AWAKENING_TRIGGER = TRIGGERS.register("activate_eye_criterion",
            () -> new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "activate_eye_criterion"), new ResourceLocation(ReactiveMod.MODID, "place_eye")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> HARVEST_TRIGGER = TRIGGERS.register("harvest_criterion",
            () -> new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "harvest_criterion"), new ResourceLocation(ReactiveMod.MODID, "see_synthesis")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_DISPLACEMENT_TRIGGER = TRIGGERS.register("see_displacement_criterion",
            () -> new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_displacement_criterion"), new ResourceLocation(ReactiveMod.MODID, "get_motion_salts")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_SLOWFALLED_TRIGGER = TRIGGERS.register("be_slowfalled_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_slowfalled_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_LEVITATED_TRIGGER = TRIGGERS.register("be_levitated_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_levitated_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_ALLAY_SUMMON_TRIGGER = TRIGGERS.register("see_allay_summon_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_allay_summon_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_CRUCIBLE_FAIL_TRIGGER = TRIGGERS.register("see_crucible_fail_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_crucible_fail_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> BE_TELEPORTED_TRIGGER = TRIGGERS.register("be_teleported_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_teleported_criterion")));

    public static final DeferredHolder<CriterionTrigger<?>, FlagTrigger> SEE_BLAZE_GATHER_TRIGGER = TRIGGERS.register("see_blaze_gather_criterion",
            () -> new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_blaze_gather_criterion")));


}
