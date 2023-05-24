package com.hyperlynx.reactive.advancements;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CriteriaTriggers {
    //Register advancement criteria for the book
    public static final FlagCriterion ENDER_PEARL_DISSOLVE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "dissolve_tp_criterion"));
    public static final FlagCriterion SEE_SYNTHESIS_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "see_synthesis_criterion"));
    public static final FlagCriterion BE_CURSED_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "be_cursed_criterion"));
    public static final FlagCriterion TRY_NETHER_CRUCIBLE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "try_nether_crucible_criterion"));
    public static final FlagCriterion TRY_LAVA_CRUCIBLE_TRIGGER = new StagedFlagCriterion(new ResourceLocation(ReactiveMod.MODID, "try_lava_crucible_criterion"), new ResourceLocation(ReactiveMod.MODID, "try_nether_crucible"));
    public static final FlagCriterion SEE_SACRIFICE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "see_sacrifice_criterion"));
    public static final FlagCriterion MAKE_RIFT_TRIGGER = new StagedFlagCriterion(new ResourceLocation(ReactiveMod.MODID, "make_rift_criterion"), new ResourceLocation(ReactiveMod.MODID, "dissolve_tp"));
    public static final FlagCriterion PORTAL_TRADE_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "portal_trade_criterion"));
    public static final FlagCriterion PORTAL_FREEZE_TRIGGER = new StagedFlagCriterion(new ResourceLocation(ReactiveMod.MODID, "portal_freeze_criterion"), new ResourceLocation(ReactiveMod.MODID, "portal_trade"));
    public static final FlagCriterion BLOCK_FALL_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "see_block_fall_criterion"));
    public static final FlagCriterion PLACE_OCCULT_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "place_eye_criterion"));
    public static final FlagCriterion OCCULT_AWAKENING_TRIGGER = new StagedFlagCriterion(new ResourceLocation(ReactiveMod.MODID, "activate_eye_criterion"), new ResourceLocation(ReactiveMod.MODID, "place_eye"));
    public static final FlagCriterion SEE_DISPLACEMENT_TRIGGER = new StagedFlagCriterion(new ResourceLocation(ReactiveMod.MODID, "see_displacement_criterion"), new ResourceLocation(ReactiveMod.MODID, "see_block_fall"));
    public static final FlagCriterion BE_SLOWFALLED_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "be_slowfalled_criterion"));
    public static final FlagCriterion BE_LEVITATED_TRIGGER = new FlagCriterion(new ResourceLocation(ReactiveMod.MODID, "be_levitated_criterion"));


    // Called in Registration.
    public static void enqueue(FMLCommonSetupEvent evt) {
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
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(BLOCK_FALL_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(OCCULT_AWAKENING_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(SEE_DISPLACEMENT_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(BE_SLOWFALLED_TRIGGER));
        evt.enqueueWork(() -> net.minecraft.advancements.CriteriaTriggers.register(BE_LEVITATED_TRIGGER));
    }
}
