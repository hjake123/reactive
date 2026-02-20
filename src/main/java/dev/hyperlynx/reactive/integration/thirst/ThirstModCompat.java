package dev.hyperlynx.reactive.integration.thirst;

import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

public class ThirstModCompat {
    public static boolean isThirstModCleanWater(ItemStack stack) {
        if(ModList.get().isLoaded("thirst")) {
            return ThirstModCompatImpl.isCleanWater(stack);
        }
        return false;
    }
    public static boolean isThirstModDirtyWater(ItemStack stack) {
        if(ModList.get().isLoaded("thirst")) {
            return ThirstModCompatImpl.isDirtyWater(stack);
        }
        return false;
    }

    public static boolean checkAndUseWaterBucket(ItemStack stack, ServerPlayer player, InteractionHand hand) {
        if(isThirstModCleanWater(stack) && stack.is(Items.WATER_BUCKET)) {
            if (player.gameMode.isSurvival()) {
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            }
            return true;
        }
        if(isThirstModDirtyWater(stack) && stack.is(Items.WATER_BUCKET)) {
            player.displayClientMessage(Component.translatable("thirst.reactive.impure_water"), true);
        }
        return false;
    }
}
