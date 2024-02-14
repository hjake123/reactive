package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.items.PowerBottleItem;
import com.hyperlynx.reactive.items.WarpBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class DivineSymbolBlock extends SymbolBlock{
    public DivineSymbolBlock(Properties props) {
        super(props);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        double d0 = source.nextDouble() * 8 - 4;
        double d1 = source.nextDouble() * 8 - 4;
        double d2 = source.nextDouble() * 8 - 4;
        level.addParticle(Registration.STARDUST_PARTICLE, pos.getX()+ d0,pos.getY()+d1, pos.getZ()+d2,0,0,0);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if(level.isClientSide)
            return InteractionResult.PASS;

        if(!(player.getItemInHand(hand).getItem() instanceof PowerBottleItem))
            return InteractionResult.PASS;

        boolean accepted = false;
        ItemStack stack = player.getItemInHand(hand);
        if(Powers.VITAL_POWER.get().matchesBottle(stack)){
            if(player.getHealth() < 20F){
                player.heal(10F);
                player.displayClientMessage(Component.translatable("message.reactive.donate_vital"), true);
                accepted = true;
            }else{
                player.displayClientMessage(Component.translatable("message.reactive.reject_vital"), true);
            }
        }else if(Powers.LIGHT_POWER.get().matchesBottle(stack)){
            if(player.getActiveEffects().stream().anyMatch(mei -> mei.getEffect().equals(MobEffects.INVISIBILITY))){
                player.removeEffect(MobEffects.INVISIBILITY);
            }else{
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1200, 0, true, false));
            }
            player.displayClientMessage(Component.translatable("message.reactive.donate_light"), true);
            accepted = true;
        }else if(Powers.WARP_POWER.get().matchesBottle(stack)){
            if(WarpBottleItem.isRiftBottle(stack)){
                boolean warp_failed = !WarpBottleItem.attemptWarp(level, player, hand);
                if(warp_failed) {
                    player.displayClientMessage(Component.translatable("message.reactive.donate_warp_failed"), true);
                }else{
                    accepted = true;
                }
            }else{
                player.displayClientMessage(Component.translatable("message.reactive.reject_warp"), true);
            }
        }else if(Powers.MIND_POWER.get().matchesBottle(stack)){
            player.addEffect(new MobEffectInstance(Registration.FAR_REACH.get(), 1200, 0, true, false));
            player.displayClientMessage(Component.translatable("message.reactive.donate_mind"), true);
            accepted = true;
        }else if(Powers.BLAZE_POWER.get().matchesBottle(stack)){
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 390, 0, true, false));
            player.setRemainingFireTicks(390);
            player.setTicksFrozen(0);
            player.displayClientMessage(Component.translatable("message.reactive.donate_blaze"), true);
            accepted = true;
        }else if(Powers.SOUL_POWER.get().matchesBottle(stack)){
            player.displayClientMessage(Component.translatable("message.reactive.donate_reject_soul"), true);
        }else{
            player.displayClientMessage(Component.translatable("message.reactive.donate_reject_generic"), true);
        }

        if(!player.isCreative() && accepted) {
            player.getItemInHand(hand).shrink(1);
            player.addItem(new ItemStack(Registration.QUARTZ_BOTTLE.get()));
        }

        return InteractionResult.SUCCESS;
    }
}
