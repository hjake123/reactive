package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.advancements.CriteriaTriggers;
import com.hyperlynx.reactive.blocks.OccultSymbolBlock;
import com.hyperlynx.reactive.fx.ParticleScribe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class AlchemyScroll extends SimpleFoiledItem {
    public AlchemyScroll(Properties props) {
        super(props);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if(level.getBlockState(context.getClickedPos()).is(Blocks.CAULDRON)){
            if(level.isClientSide){
                ParticleScribe.drawParticleRing(level, Registration.RUNE_PARTICLE, context.getClickedPos(), 0.7, 0.9, 50);
            }else{
                level.setBlock(context.getClickedPos(), Registration.CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                level.playSound(null, context.getClickedPos(), SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS, 1.0F, 0.8F);
            }

            if(!context.getPlayer().isCreative())
                context.getPlayer().setItemInHand(context.getHand(), Items.PAPER.getDefaultInstance());
            return InteractionResult.SUCCESS;
        }
        if(level.getBlockState(context.getClickedPos()).is(Registration.OCCULT_SYMBOL.get()) && !level.getBlockState(context.getClickedPos()).getValue(OccultSymbolBlock.ACTIVE)){
            if(level.isClientSide){
                for(int i = 0; i < 10; i++){
                    level.addParticle(Registration.SMALL_BLACK_RUNE_PARTICLE,
                            context.getClickLocation().x + level.random.nextDouble()*0.5-0.25,
                            context.getClickLocation().y + level.random.nextDouble()*0.5-0.25,
                            context.getClickLocation().z + level.random.nextDouble()*0.5-0.25,
                            0, 0, 0);
                }
            }else{
                level.setBlock(context.getClickedPos(), level.getBlockState(context.getClickedPos()).setValue(OccultSymbolBlock.ACTIVE, true), Block.UPDATE_CLIENTS);
                level.playSound(null, context.getClickedPos(), SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS, 1.0F, 0.74F);
                CriteriaTriggers.OCCULT_AWAKENING_TRIGGER.trigger((ServerPlayer) context.getPlayer());
            }

            if(!context.getPlayer().isCreative())
                context.getPlayer().setItemInHand(context.getHand(), Items.PAPER.getDefaultInstance());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
