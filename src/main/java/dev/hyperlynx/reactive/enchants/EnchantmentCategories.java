package dev.hyperlynx.reactive.enchants;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.items.DisplacerItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantmentCategories {
    public static EnchantmentCategory SPEED_COMPATIBLE_STAVES = EnchantmentCategory.create("speed_able_staves",
            (item -> item.getDefaultInstance().is(ItemTags.create(ReactiveMod.location("enchantable/staff_tick")))));

    public static EnchantmentCategory POTENCY_COMPATIBLE_STAVES = EnchantmentCategory.create("attack_able_staves",
            (item -> item.getDefaultInstance().is(ItemTags.create(ReactiveMod.location("enchantable/staff_attack")))));

    public static EnchantmentCategory AOE_COMPATIBLE_STAVES = EnchantmentCategory.create("range_able_staves",
            (item -> item.getDefaultInstance().is(ItemTags.create(ReactiveMod.location("enchantable/staff_aoe")))));

    public static EnchantmentCategory DISPLACER = EnchantmentCategory.create("displacer",
            (item -> item instanceof DisplacerItem));
}
