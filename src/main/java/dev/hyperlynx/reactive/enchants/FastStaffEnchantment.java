package dev.hyperlynx.reactive.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class FastStaffEnchantment extends Enchantment {
    public FastStaffEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategories.SPEED_COMPATIBLE_STAVES, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxCost(int level) {
        return 45 + 9 * level;
    }

    @Override
    public int getMinCost(int level) {
        return 2 + 8 * level;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "enchantment.reactive.fast_staff";
    }

    public static int adjustStaffTick(int base_tick_frequency, int level){
        return base_tick_frequency - level;
    }
}
