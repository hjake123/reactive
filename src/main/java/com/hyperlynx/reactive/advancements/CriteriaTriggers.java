package com.hyperlynx.reactive.advancements;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class CriteriaTriggers {
    //Register advancement criteria for the book
    public static final FlagTrigger MAKE_CRUCIBLE_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "make_crucible_criterion"));
    public static final FlagTrigger ENDER_PEARL_DISSOLVE_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "dissolve_tp_criterion"));
    public static final FlagTrigger SEE_SYNTHESIS_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_synthesis_criterion"));
    public static final FlagTrigger BE_CURSED_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_cursed_criterion"));
    public static final FlagTrigger TRY_NETHER_CRUCIBLE_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "try_nether_crucible_criterion"));
    public static final FlagTrigger TRY_LAVA_CRUCIBLE_TRIGGER = new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "try_lava_crucible_criterion"), new ResourceLocation(ReactiveMod.MODID, "try_nether_crucible"));
    public static final FlagTrigger SEE_SACRIFICE_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_sacrifice_criterion"));
    public static final FlagTrigger MAKE_RIFT_TRIGGER = new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "make_rift_criterion"), new ResourceLocation(ReactiveMod.MODID, "dissolve_tp"));
    public static final FlagTrigger PORTAL_TRADE_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "portal_trade_criterion"));
    public static final FlagTrigger PORTAL_FREEZE_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "portal_freeze_criterion"));
    public static final FlagTrigger PLACE_OCCULT_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "place_eye_criterion"));
    public static final FlagTrigger OCCULT_AWAKENING_TRIGGER = new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "activate_eye_criterion"), new ResourceLocation(ReactiveMod.MODID, "place_eye"));
    public static final FlagTrigger HARVEST_TRIGGER = new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "harvest_criterion"), new ResourceLocation(ReactiveMod.MODID, "see_synthesis"));
    public static final FlagTrigger SEE_DISPLACEMENT_TRIGGER = new StagedFlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_displacement_criterion"), new ResourceLocation(ReactiveMod.MODID, "get_motion_salts"));
    public static final FlagTrigger BE_SLOWFALLED_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_slowfalled_criterion"));
    public static final FlagTrigger BE_LEVITATED_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_levitated_criterion"));
    public static final FlagTrigger SEE_ALLAY_SUMMON_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_allay_summon_criterion"));
    public static final FlagTrigger SEE_CRUCIBLE_FAIL_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_crucible_fail_criterion"));
    public static final FlagTrigger BE_TELEPORTED_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "be_teleported_criterion"));
    public static final FlagTrigger SEE_BLAZE_GATHER_TRIGGER = new FlagTrigger(new ResourceLocation(ReactiveMod.MODID, "see_blaze_gather_criterion"));

    // Called in Registration.
    public static void enqueue(FMLCommonSetupEvent evt) {
        evt.enqueueWork(() -> register(MAKE_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> register(ENDER_PEARL_DISSOLVE_TRIGGER));
        evt.enqueueWork(() -> register(SEE_SYNTHESIS_TRIGGER));
        evt.enqueueWork(() -> register(BE_CURSED_TRIGGER));
        evt.enqueueWork(() -> register(TRY_NETHER_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> register(TRY_LAVA_CRUCIBLE_TRIGGER));
        evt.enqueueWork(() -> register(SEE_SACRIFICE_TRIGGER));
        evt.enqueueWork(() -> register(MAKE_RIFT_TRIGGER));
        evt.enqueueWork(() -> register(PORTAL_TRADE_TRIGGER));
        evt.enqueueWork(() -> register(PORTAL_FREEZE_TRIGGER));
        evt.enqueueWork(() -> register(PLACE_OCCULT_TRIGGER));
        evt.enqueueWork(() -> register(OCCULT_AWAKENING_TRIGGER));
        evt.enqueueWork(() -> register(SEE_DISPLACEMENT_TRIGGER));
        evt.enqueueWork(() -> register(BE_SLOWFALLED_TRIGGER));
        evt.enqueueWork(() -> register(BE_LEVITATED_TRIGGER));
        evt.enqueueWork(() -> register(SEE_ALLAY_SUMMON_TRIGGER));
        evt.enqueueWork(() -> register(SEE_CRUCIBLE_FAIL_TRIGGER));
        evt.enqueueWork(() -> register(BE_TELEPORTED_TRIGGER));
        evt.enqueueWork(() -> register(HARVEST_TRIGGER));
        evt.enqueueWork(() -> register(SEE_BLAZE_GATHER_TRIGGER));
        ReactionMan.CRITERIA_BUILDER.register(evt);
    }

    static void register(FlagTrigger trigger){
        net.minecraft.advancements.CriteriaTriggers.register(trigger.path(), trigger);
    }
}
