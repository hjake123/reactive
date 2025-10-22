package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.StaffBlockEntity;
import dev.hyperlynx.reactive.enchants.FastStaffEnchantment;
import dev.hyperlynx.reactive.enchants.StrongStaffEnchantment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StaffItem extends BlockItem {
    Consumer<Player> effectFunction;
    boolean beam; // Whether the effect should render as a beam (true) or zap (false).
    Supplier<Integer> frequency; // Beam abilities activate once in this many ticks.
    public Item repair_item;

    public StaffItem(Block block, Properties props, Consumer<Player> effect, boolean beam, Supplier<Integer> frequency, Item repair_item) {
        super(block, props);
        effectFunction = effect;
        this.beam = beam;
        this.repair_item = repair_item;
        this.frequency = frequency;
    }

    public static boolean onLastDurability(ItemStack stack){
        return stack.getDamageValue() == stack.getMaxDamage() - 1;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int ticks) {
        if(onLastDurability(stack))
            return;
        if(ticks % getFrequency(stack) == 1) {
            if(level.isClientSide && !beam)
                effectFunction.accept((Player) player);

            if(!level.isClientSide) {
                level.gameEvent(GameEvent.PROJECTILE_SHOOT, player.getEyePosition(), GameEvent.Context.of(player));
                effectFunction.accept((Player) player);
                if (player.getOffhandItem().is(stack.getItem())) {
                    player.getOffhandItem().hurtAndBreak(1, player, (LivingEntity l) -> {});
                } else {
                    player.getMainHandItem().hurtAndBreak(1, player, (LivingEntity l) -> {});
                }
            }
        }
        if (level.isClientSide && beam) effectFunction.accept((Player) player);
    }

    @SuppressWarnings("deprecation") // Minecraft itself will never change on this branch.
    private int getFrequency(@NotNull ItemStack stack){
        int base_frequency = frequency.get();
        int enchant_level = EnchantmentHelper.getItemEnchantmentLevel(Registration.FAST_STAFF.get(), stack);
        if(enchant_level > 0){
            return FastStaffEnchantment.adjustStaffTick(base_frequency, enchant_level);
        }
        return base_frequency;
    }

    public static float getDamageAmount(LivingEntity user, float base_damage){
        int enchant_level = EnchantmentHelper.getEnchantmentLevel(Registration.POTENCY.get(), user);
        if(enchant_level > 0){
            return StrongStaffEnchantment.adjustStaffPower(base_damage, enchant_level);
        }
        return base_damage;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 20;
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
        if(onLastDurability(player.getItemInHand(hand)))
            return InteractionResultHolder.fail(player.getItemInHand(hand));
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

    @Override
    public boolean isRepairable(ItemStack stack) {
        return true;
    }

    // Check if the item being used to repair is the assigned repair bottle for this staff.
    @Override
    public boolean isValidRepairItem(ItemStack self, ItemStack repair_item_candidate) {
        return repair_item != null && repair_item_candidate.is(repair_item);
    }

    // Called when the item is placed to store item stack data into the block entity.
    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player placer, ItemStack stack, BlockState state) {
        MinecraftServer server = level.getServer();
        if (server == null)
            return false;

        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity == null)
            return false;

        CompoundTag data_tag = blockentity.saveWithoutMetadata();
        CompoundTag prior_data_tag = data_tag.copy();
        data_tag.put(StaffBlockEntity.ITEM_STACK_TAG, stack.hasTag() ? Objects.requireNonNull(stack.getTag()) : new CompoundTag());

        if (!data_tag.equals(prior_data_tag)) {
            blockentity.load(data_tag);
            blockentity.setChanged();
            return true;
        }

        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
