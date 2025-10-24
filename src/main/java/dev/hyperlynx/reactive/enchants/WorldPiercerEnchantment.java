package dev.hyperlynx.reactive.enchants;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class WorldPiercerEnchantment extends Enchantment {
    public WorldPiercerEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategories.DISPLACER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxCost(int level) {
        return 25;
    }

    @Override
    public int getMinCost(int level) {
        return 16;
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "enchantment.reactive.world_piercer";
    }
}
