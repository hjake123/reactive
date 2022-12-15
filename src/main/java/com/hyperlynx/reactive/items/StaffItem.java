package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.StaffBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;

public class StaffItem extends BlockItem {
    Function<Player, Player> effectFunction;
    boolean beam; // Whether the effect should render as a beam (true) or zap (false).

    public StaffItem(Block block, Properties props, Function<Player, Player> effect, boolean beam) {
        super(block, props);
        effectFunction = effect;
        this.beam = beam;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int ticks) {
        if(ticks % 10 == 1) {
            if(level.isClientSide && !beam)
                effectFunction.apply((Player) player);

            if(!level.isClientSide)
                effectFunction.apply((Player) player);

        }

        if(level.isClientSide) {
            if(beam)
                effectFunction.apply((Player) player);
            return;
        }

        if (player.getOffhandItem().is(stack.getItem())) {
            player.getOffhandItem().hurtAndBreak(1, player, (LivingEntity l) -> {});
        } else {
            player.getMainHandItem().hurtAndBreak(1, player, (LivingEntity l) -> {});
        }

    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!player.isCrouching())
            player.startUsingItem(hand);
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getPlayer() == null)
            return InteractionResult.SUCCESS;
        if(context.getPlayer().isCrouching())
            return super.useOn(context);
        return InteractionResult.PASS;
    }

    // Called when the item is placed to store durability data into the block entity.
    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player placer, ItemStack stack, BlockState state) {
        MinecraftServer server = level.getServer();
        if (server == null)
            return false;

        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity == null)
            return false;

        IntTag durability_tag = IntTag.valueOf(this.getDamage(stack));

        CompoundTag data_tag = blockentity.saveWithoutMetadata();
        CompoundTag prior_data_tag = data_tag.copy();
        data_tag.put(StaffBlockEntity.DURABILITY_TAG, durability_tag);

        if (!data_tag.equals(prior_data_tag)) {
            blockentity.load(data_tag);
            blockentity.setChanged();
            return true;
        }

        return false;
    }
}
