package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import com.hyperlynx.reactive.client.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VortexStoneItem extends Item {
    private static final double STRENGTH = 0.45;

    public VortexStoneItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        int cost = 1;
        if(player.isCrouching() && player.onGround()){
            if(player.onGround()){
                SpecialCaseMan.windBomb(level, player.position(), 0.8F);
                cost = 4;
            }
        } else {
            Vec3 impulse = player.getLookAngle().scale(STRENGTH);
            var new_movement = player.getDeltaMovement().add(impulse);
            if(new_movement.length() > 2){
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            player.setDeltaMovement(new_movement);
            player.resetFallDistance();
            level.playSound(null, player.blockPosition(), SoundEvents.BREEZE_CHARGE, SoundSource.PLAYERS);
            ParticleScribe.drawParticle(level, ParticleTypes.GUST_EMITTER_SMALL, player.getX(), player.getY(), player.getZ());
        }
        if(!player.hasInfiniteMaterials()){
            var stack = player.getItemInHand(hand);
            stack.hurtAndBreak(cost, player, LivingEntity.getSlotForHand(hand));
            if(stack.getDamageValue() == stack.getMaxDamage() - 1){
                player.setItemInHand(hand, Items.PACKED_ICE.getDefaultInstance());
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
