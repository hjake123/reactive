package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AlchemyTags {
    public static final TagKey<Item> blazeSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "blaze_sources"));
    public static final TagKey<Item> mindSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "mind_sources"));
    public static final TagKey<Item> soulSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "soul_sources"));
    public static final TagKey<Item> curseSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "curse_sources"));
    public static final TagKey<Item> lightSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "light_sources"));
    public static final TagKey<Item> warpSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "warp_sources"));
    public static final TagKey<Item> acidSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "caustic_sources"));
    public static final TagKey<Item> vitalSource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "vital_sources"));
    public static final TagKey<Item> bodySource = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "body_sources"));
    public static final TagKey<Item> highPower = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "high_potency"));

}
