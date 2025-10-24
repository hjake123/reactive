package dev.hyperlynx.reactive.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class AOEStaffEnchantment extends Enchantment {
    public AOEStaffEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategories.AOE_COMPATIBLE_STAVES, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxCost(int level) {
        return 20;
    }

    @Override
    public int getMinCost(int level) {
        return 12;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "enchantment.reactive.super_missile";
    }
}
