package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.client.particles.ParticleScribe;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StardustItem extends Item {
    final int MAX_CHAIN_DEPTH = 10;
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
                place(level, player, player.getOnPos().above(2), hand);
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private static void place(Level level, Player player, BlockPos pos, @NotNull InteractionHand hand) {
        level.setBlock(pos, Registration.STARDUST.get().defaultBlockState(), 2);
        if(!player.isCreative())
            player.getItemInHand(hand).setCount(player.getItemInHand(hand).getCount() - 1);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(!level.getBlockState(pos).is(Registration.STARDUST.get())){
            return InteractionResult.PASS;
        }

        for(int i = 0; i < MAX_CHAIN_DEPTH; i++){
            if(level.getBlockState(pos).is(Registration.STARDUST.get())){
                int x_displacement = WorldSpecificValue.get(pos +"x_displace", -5, 5);
                int y_displacement = WorldSpecificValue.get(pos +"y_displace", -3, 4);
                int z_displacement = WorldSpecificValue.get(pos +"z_displace", -5, 5);
                BlockPos new_pos = pos.offset(x_displacement, y_displacement, z_displacement);
                ParticleScribe.drawParticleLine(level, Registration.STARDUST_PARTICLE, pos, new_pos, 5, 0.1);
                pos = new_pos;
            }else if(level.getBlockState(pos).isAir()){
                place(level, Objects.requireNonNull(context.getPlayer()), pos, context.getHand());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }
}
