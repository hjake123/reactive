package dev.hyperlynx.reactive.advancements;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CriteriaTriggers {
    //Register advancement criteria for the book
    public static final FlagCriterion MAKE_CRUCIBLE_TRIGGER = new FlagCriterion(ReactiveMod.location("make_crucible_criterion"));
    public static final FlagCriterion ENDER_PEARL_DISSOLVE_TRIGGER = new FlagCriterion(ReactiveMod.location("dissolve_tp_criterion"));
    public static final FlagCriterion SEE_SYNTHESIS_TRIGGER = new FlagCriterion(ReactiveMod.location("see_synthesis_criterion"));
    public static final FlagCriterion BE_CURSED_TRIGGER = new FlagCriterion(ReactiveMod.location("be_cursed_criterion"));
    public static final FlagCriterion TRY_NETHER_CRUCIBLE_TRIGGER = new FlagCriterion(ReactiveMod.location("try_nether_crucible_criterion"));
    public static final FlagCriterion TRY_LAVA_CRUCIBLE_TRIGGER = new StagedFlagCriterion(ReactiveMod.location("try_lava_crucible_criterion"), ReactiveMod.location("try_nether_crucible"));
    public static final FlagCriterion SEE_SACRIFICE_TRIGGER = new FlagCriterion(ReactiveMod.location("see_sacrifice_criterion"));
    public static final FlagCriterion MAKE_RIFT_TRIGGER = new StagedFlagCriterion(ReactiveMod.location("make_rift_criterion"), ReactiveMod.location("dissolve_tp"));
    public static final FlagCriterion PORTAL_TRADE_TRIGGER = new FlagCriterion(ReactiveMod.location("portal_trade_criterion"));
    public static final FlagCriterion PORTAL_FREEZE_TRIGGER = new FlagCriterion(ReactiveMod.location("portal_freeze_criterion"));
    public static final FlagCriterion PLACE_OCCULT_TRIGGER = new FlagCriterion(ReactiveMod.location("place_eye_criterion"));
    public static final FlagCriterion OCCULT_AWAKENING_TRIGGER = new StagedFlagCriterion(ReactiveMod.location("activate_eye_criterion"), ReactiveMod.location("place_eye"));
    public static final FlagCriterion HARVEST_TRIGGER = new StagedFlagCriterion(ReactiveMod.location("harvest_criterion"), ReactiveMod.location("see_synthesis"));
    public static final FlagCriterion SEE_DISPLACEMENT_TRIGGER = new StagedFlagCriterion(ReactiveMod.location("see_displacement_criterion"), ReactiveMod.location("get_motion_salts"));
    public static final FlagCriterion BE_SLOWFALLED_TRIGGER = new FlagCriterion(ReactiveMod.location("be_slowfalled_criterion"));
    public static final FlagCriterion BE_LEVITATED_TRIGGER = new FlagCriterion(ReactiveMod.location("be_levitated_criterion"));
    public static final FlagCriterion SEE_ALLAY_SUMMON_TRIGGER = new FlagCriterion(ReactiveMod.location("see_allay_summon_criterion"));
    public static final FlagCriterion SEE_CRUCIBLE_FAIL_TRIGGER = new FlagCriterion(ReactiveMod.location("see_crucible_fail_criterion"));
    public static final FlagCriterion BE_TELEPORTED_TRIGGER = new FlagCriterion(ReactiveMod.location("be_teleported_criterion"));
    public static final FlagCriterion SEE_BLAZE_GATHER_TRIGGER = new FlagCriterion(ReactiveMod.location("see_blaze_gather_criterion"));
    public static final FlagCriterion UNDEAD_PLAYER_DIVINE_HURT = new FlagCriterion(ReactiveMod.location("undead_player_divine_hurt_criterion"));
    public static final FlagCriterion DISCOVER_MATERIAL = new FlagCriterion(ReactiveMod.location("discover_material_criterion"));
    public static final FlagCriterion GOLD_THREAD_REACTION = new FlagCriterion(ReactiveMod.location("see_gold_thread_reaction_criterion"));
    public static final FlagCriterion THROW_FLASK = new FlagCriterion(ReactiveMod.location("throw_flask_criterion"));
    public static final FlagCriterion ISOLATE_OMEN = new FlagCriterion(ReactiveMod.location("isolate_omen_criterion"));
    public static final ReactionCriterion REACTION_TRIGGER = new ReactionCriterion(ReactiveMod.location("reaction"));
    public static final ReactionCriterion PERFECT_REACTION_TRIGGER = new ReactionCriterion(ReactiveMod.location("perfect_reaction"));


    // Called in Registration.
    public static void enqueue(FMLCommonSetupEvent evt) {
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(MAKE_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(ENDER_PEARL_DISSOLVE_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SEE_SYNTHESIS_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(BE_CURSED_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(TRY_NETHER_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(TRY_LAVA_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SEE_SACRIFICE_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(MAKE_RIFT_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(PORTAL_TRADE_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(PORTAL_FREEZE_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(PLACE_OCCULT_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(OCCULT_AWAKENING_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SEE_DISPLACEMENT_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(BE_SLOWFALLED_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(BE_LEVITATED_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SEE_ALLAY_SUMMON_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SEE_CRUCIBLE_FAIL_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(BE_TELEPORTED_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(HARVEST_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SEE_BLAZE_GATHER_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(UNDEAD_PLAYER_DIVINE_HURT));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(REACTION_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(PERFECT_REACTION_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(DISCOVER_MATERIAL));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(GOLD_THREAD_REACTION));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(THROW_FLASK));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(ISOLATE_OMEN));
    }
}
