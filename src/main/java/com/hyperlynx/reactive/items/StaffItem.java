package com.hyperlynx.reactive.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class StaffItem extends BlockItem {
    Function<Player, Player> effectFunction;

    public StaffItem(Block block, Properties props, Function<Player, Player> effect) {
        super(block, props);
        effectFunction = effect;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        effectFunction.apply((Player) player);
        stack.hurt(1, RandomSource.create(), (ServerPlayer) player);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CROSSBOW;
    }
}
