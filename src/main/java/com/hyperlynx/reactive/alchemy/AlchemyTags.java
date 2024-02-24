package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AlchemyTags {
    public static final TagKey<Item> highPower = ItemTags.create(new ResourceLocation(ReactiveMod.MODID, "high_potency"));
    public static final TagKey<Block> acidImmune = BlockTags.create(new ResourceLocation(ReactiveMod.MODID, "acid_immune"));
    public static final TagKey<Block> canBeGenerated = BlockTags.create(new ResourceLocation(ReactiveMod.MODID, "can_be_generated"));
    public static final TagKey<Block> notRelocatable = BlockTags.create(new ResourceLocation("forge", "relocation_not_supported"));

}
