package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.be.StaffBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class StaffItem extends BlockItem {
    BiConsumer<Player, ItemStack> effectFunction;
    boolean beam; // Whether the effect should render as a beam (true) or zap (false).
    private final int frequency; // Beam abilities activate once in this many ticks.
    public Item repair_item;

    public StaffItem(Block block, Properties props, BiConsumer<Player, ItemStack> effect, boolean beam, int frequency, Item repair_item) {
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
                effectFunction.accept((Player) player, stack);

            if(!level.isClientSide) {
                effectFunction.accept((Player) player, stack);
                if (player.getOffhandItem().is(stack.getItem())) {
                    player.getOffhandItem().hurtAndBreak(1, (ServerLevel) level, player, (i) -> {});
                } else {
                    player.getMainHandItem().hurtAndBreak(1, (ServerLevel) level, player, (i) -> {});
                }
            }
        }
        if (level.isClientSide && beam) effectFunction.accept((Player) player, stack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity wielder) {
        return 72000;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 20;
    }

    private int getFrequency(@NotNull ItemStack stack){
        MutableInt frequency = new MutableInt(this.frequency);

        EnchantmentHelper.runIterationOnItem(stack, (enchant, enchant_level) -> {
            for(var effect : enchant.value().getEffects(Registration.STAFF_RATE.value())){
                frequency.setValue(effect.effect().process(enchant_level, RandomSource.create(), frequency.getValue()));
            }
        });

        return frequency.getValue();
    }

    public static void hurtVictim(@NotNull ServerPlayer player, @NotNull ItemStack stack, @NotNull Entity victim, @NotNull DamageSource damage_source, float unmodified_damage) {
        victim.hurt(damage_source, getModifiedDamageOutput(player, stack, victim, damage_source, unmodified_damage));
    }

    private static float getModifiedDamageOutput(ServerPlayer player, @NotNull ItemStack stack, Entity target, DamageSource damage_source, float unmodified_damage) {
        return getModifiedDamageOutput(player.serverLevel(), stack, target, damage_source, unmodified_damage);
    }

    private static float getModifiedDamageOutput(ServerLevel server, @NotNull ItemStack stack, Entity target, DamageSource damage_source, float unmodified_damage){
        MutableFloat strength = new MutableFloat(unmodified_damage);
        EnchantmentHelper.runIterationOnItem(stack, (enchant, enchant_level) -> Enchantment.applyEffects(
            enchant.value().getEffects(Registration.STAFF_DAMAGE.value()),
            Enchantment.damageContext(server, enchant_level, target, damage_source),
            (effect) -> strength.setValue(effect.process(enchant_level, server.random, strength.getValue()))
        ));
        return strength.getValue();
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
        if (!(blockentity instanceof StaffBlockEntity staff))
            return false;

        staff.stack = stack.copy();
        return true;
    }
}
