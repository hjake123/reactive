package com.hyperlynx.reactive.alchemy;

import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AlchemyTags {
    public static final TagKey<Item> highPower = ItemTags.create(ReactiveMod.location("high_potency"));
    public static final TagKey<Block> acidImmune = BlockTags.create(ReactiveMod.location("acid_immune"));
    public static final TagKey<Block> canBeGenerated = BlockTags.create(ReactiveMod.location("can_be_generated"));
    public static final TagKey<Block> doNotDisplace = BlockTags.create(ReactiveMod.location("do_not_displace"));
    public static final TagKey<Block> doNotBlockFall = BlockTags.create(ReactiveMod.location("do_not_make_fall"));
    public static final TagKey<Block> displaceConductive = BlockTags.create(ReactiveMod.location("displacement_conductive"));
    public static final TagKey<Block> notRelocatable = BlockTags.create(ResourceLocation.fromNamespaceAndPath("neoforge", "relocation_not_supported"));

}
