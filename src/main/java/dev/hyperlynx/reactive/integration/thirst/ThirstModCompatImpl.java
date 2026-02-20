package dev.hyperlynx.reactive.integration.thirst;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.item.ItemStack;

public class ThirstModCompatImpl {
    public static boolean isCleanWater(ItemStack stack) {
        return WaterPurity.isWaterFilledContainer(stack) && WaterPurity.getPurity(stack) >= 2;
    }

    public static boolean isDirtyWater(ItemStack stack) {
        return WaterPurity.isWaterFilledContainer(stack) && WaterPurity.getPurity(stack) < 2;
    }
}
