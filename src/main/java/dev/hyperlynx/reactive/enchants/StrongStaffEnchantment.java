package dev.hyperlynx.reactive.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class StrongStaffEnchantment extends Enchantment {
    public StrongStaffEnchantment() {
        super(Rarity.COMMON, EnchantmentCategories.POTENCY_COMPATIBLE_STAVES, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxCost(int level) {
        return 51 + 10 * level;
    }

    @Override
    public int getMinCost(int level) {
        return 5 + 5 * level;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "enchantment.reactive.potency";
    }

    public static float adjustStaffPower(float base_power, int level) {
        return base_power + (0.2F * (level - 1)) * base_power + 1.25F;
    }
}
