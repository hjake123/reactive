package com.hyperlynx.reactive.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.stringtemplate.v4.ST;

public class ForceRockItem extends Item {
    private static final double STRENGTH = 0.18;

    public ForceRockItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Vec3 impulse = player.getLookAngle().scale(STRENGTH);
        player.setDeltaMovement(player.getDeltaMovement().add(impulse));
        player.getCooldowns().addCooldown(this, 6);
        if(!player.isCreative() && level.random.nextFloat() < 0.7){
            player.getItemInHand(hand).shrink(1);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CALCITE_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CALCITE_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
