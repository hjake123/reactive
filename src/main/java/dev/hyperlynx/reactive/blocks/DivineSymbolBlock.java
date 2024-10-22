package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.items.PowerBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if(level.isClientSide)
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        if(!(player.getItemInHand(hand).getItem() instanceof PowerBottleItem))
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        boolean accepted = false;
        BlockPos player_start_pos = player.blockPosition().above(); // Captured because the player might teleport.

        if(Powers.VITAL_POWER.get().matchesBottle(stack)){
            if(player.getHealth() < 20F){
                player.heal(20F);
                player.displayClientMessage(Component.translatable("message.reactive.donate_vital"), true);
                accepted = true;
            }else{
                player.displayClientMessage(Component.translatable("message.reactive.reject_vital"), true);
            }
        }else if(Powers.LIGHT_POWER.get().matchesBottle(stack)){
            if(player.getActiveEffects().stream().anyMatch(mei -> mei.getEffect().equals(MobEffects.INVISIBILITY))){
                player.removeEffect(MobEffects.INVISIBILITY);
            }else{
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 2400, 0, true, false));
            }
            player.displayClientMessage(Component.translatable("message.reactive.donate_light"), true);
            accepted = true;
        }else if(Powers.WARP_POWER.get().matchesBottle(stack)){
            player.addEffect(new MobEffectInstance(Registration.HIGH_STEP, 12000, 0, true, false));
            player.displayClientMessage(Component.translatable("message.reactive.donate_warp"), true);
            accepted = true;
        }else if(Powers.MIND_POWER.get().matchesBottle(stack)){
            player.addEffect(new MobEffectInstance(Registration.FAR_REACH, 2800, 0, true, false));
            player.displayClientMessage(Component.translatable("message.reactive.donate_mind"), true);
            accepted = true;
        }else if(Powers.BLAZE_POWER.get().matchesBottle(stack)){
            if(player.getTicksFrozen() > 0)
                player.setTicksFrozen(0);
            else {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false));
                player.addEffect(new MobEffectInstance(Registration.FIRE_SHIELD, 1900, 0, true, false));
                player.setRemainingFireTicks(100);
            }
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

        if(accepted){
            ParticleScribe.drawParticleZigZag(level, Registration.STARDUST_PARTICLE, pos, player_start_pos, 4, 5, 0.4);
            player.getCooldowns().addCooldown(stack.getItem(), 100);
        }

        return InteractionResult.SUCCESS;
    }
}
