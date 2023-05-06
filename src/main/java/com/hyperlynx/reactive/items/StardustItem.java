package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.blocks.StardustBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StardustItem extends Item {
    public StardustItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        if(level.dimension().equals(Level.END)){
            if(level.isClientSide){
                for(int i = 0; i < 24; i++){
                    level.addParticle(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.5, player.getZ(), level.random.nextDouble()*0.1 - 0.05, level.random.nextDouble()*0.1 - 0.05, level.random.nextDouble()*0.1 - 0.05);
                }
            }
        }else{
            if(level.getBlockState(player.getOnPos().above(2)).isAir()){
                level.setBlock(player.getOnPos().above(2), Registration.STARDUST.get().defaultBlockState(), 2);
                if(!player.isCreative())
                    player.getItemInHand(hand).setCount(player.getItemInHand(hand).getCount() - 1);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
