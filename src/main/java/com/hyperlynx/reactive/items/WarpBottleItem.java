package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WarpBottleItem extends PowerBottleItem{
    public static final String TAG_TELEPORT_DESTINATION = "TeleportPos";
    public static final String TAG_TELEPORT_DIMENSION = "TeleportDimension";

    public WarpBottleItem(Properties props, Block block) {
        super(props, block);
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        if(isRiftBottle(player.getItemInHand(hand))){
            if(attemptWarp(level, player, hand) && !player.isCreative())
                player.setItemInHand(hand, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    public static boolean attemptWarp(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        boolean warp_occurred = false;
        CompoundTag tag = player.getItemInHand(hand).getTag();
        if(tag != null && level.dimension().equals(getTeleportDimension(tag).orElse(null))){
            GlobalPos destination = getTeleportPosition(tag);
            if(destination != null) {
                if (CrystalIronItem.effectNotBlocked(player, 1)) {
                    player.teleportTo(destination.pos().getX() + 0.5, destination.pos().getY() + 0.85, destination.pos().getZ() + 0.5);
                    level.playSound(null, destination.pos(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    warp_occurred = true;
                    if(player instanceof ServerPlayer splayer)
                        CriteriaTriggers.BE_TELEPORTED_TRIGGER.trigger(splayer);
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

    public static void addTeleportTags(ResourceKey<Level> lkey, BlockPos pos, CompoundTag tag) {
        tag.put(TAG_TELEPORT_DESTINATION, NbtUtils.writeBlockPos(pos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, lkey).result().ifPresent((dim_key) -> tag.put(TAG_TELEPORT_DIMENSION, dim_key));
    }

    public static boolean isRiftBottle(ItemStack stack){
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.contains(TAG_TELEPORT_DESTINATION);
    }

    private static Optional<ResourceKey<Level>> getTeleportDimension(CompoundTag p_40728_) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get(TAG_TELEPORT_DIMENSION)).result();
    }

    public static GlobalPos getTeleportPosition(CompoundTag p_220022_) {
        boolean flag = p_220022_.contains(TAG_TELEPORT_DESTINATION);
        boolean flag1 = p_220022_.contains(TAG_TELEPORT_DIMENSION);
        if (flag && flag1) {
            Optional<ResourceKey<Level>> optional = getTeleportDimension(p_220022_);
            if (optional.isPresent()) {
                BlockPos blockpos = NbtUtils.readBlockPos(p_220022_.getCompound(TAG_TELEPORT_DESTINATION));
                return GlobalPos.of(optional.get(), blockpos);
            }
        }
        return null;
    }

}
