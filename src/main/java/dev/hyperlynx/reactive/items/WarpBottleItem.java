package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.components.WarpBottleTarget;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class WarpBottleItem extends PowerBottleItem{
    public WarpBottleItem(Properties props, Block block) {
        super(props, block);
    }

    // Make a Warp Bottle into a Rift Bottle.
    public static ItemStack makeRiftBottle(CrucibleBlockEntity c, ItemStack bottle){
        setTeleportTarget(bottle, GlobalPos.of(Objects.requireNonNull(c.getLevel()).dimension(), c.getBlockPos()));
        c.enderRiftStrength = 0;
        return bottle;
    }

    @Override
    public @NotNull Component getDescription() {
        return super.getDescription();
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isRiftBottle(stack) || super.isFoil(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).is(Registration.DIVINE_SYMBOL.get())){
            return InteractionResult.FAIL;
        }else if(isRiftBottle(context.getItemInHand())){
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        if(isRiftBottle(player.getItemInHand(hand))){
            if(attemptWarp(level, player, hand) && !player.isCreative())
                player.setItemInHand(hand, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, hand);
    }

    public static boolean attemptWarp(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        boolean warp_occurred = false;
        var stack = player.getItemInHand(hand);
        if(level.dimension().equals(getTeleportDimension(stack).orElse(null))){
            GlobalPos destination = getTeleportPosition(stack);
            if(destination != null) {
                if (CrystalIronItem.effectNotBlocked(player, 1)) {
                    player.teleportTo(destination.pos().getX() + 0.5, destination.pos().getY() + 0.85, destination.pos().getZ() + 0.5);
                    level.playSound(null, destination.pos(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    warp_occurred = true;
                    if(player instanceof ServerPlayer splayer)
                        Registration.BE_TELEPORTED_TRIGGER.get().trigger(splayer);
                }else if(!(level instanceof ServerLevel)){
                    player.displayClientMessage(Component.translatable("message.reactive.warp_blocked"), true);
                }
            }
        }
        return warp_occurred;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        if(isRiftBottle(stack))
            return 1;
        return super.getMaxStackSize(stack);
    }

    public static void setTeleportTarget(ItemStack stack, GlobalPos target) {
        stack.set(Registration.WARP_BOTTLE_TARGET.get(), new WarpBottleTarget(target));
    }

    public static boolean isRiftBottle(ItemStack stack){
        return stack.has(Registration.WARP_BOTTLE_TARGET.value());
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return isRiftBottle(stack) ? Component.translatable("item.reactive.linked_warp_bottle") : super.getName(stack);
    }

    private static Optional<ResourceKey<Level>> getTeleportDimension(ItemStack stack) {
        if (stack.has(Registration.WARP_BOTTLE_TARGET.get())) {
            return Optional.of(Objects.requireNonNull(stack.get(Registration.WARP_BOTTLE_TARGET.get())).target().dimension());
        }
        return Optional.empty();
    }

    public static GlobalPos getTeleportPosition(ItemStack stack) {
        WarpBottleTarget target = stack.get(Registration.WARP_BOTTLE_TARGET.get());
        if(target == null){
            return null;
        }
        return target.target();
    }

}
