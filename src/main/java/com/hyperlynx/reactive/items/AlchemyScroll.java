package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.Helper;
import com.ibm.icu.text.MessagePattern;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Objects;

public class AlchemyScroll extends SimpleFoiledItem {
    public AlchemyScroll(Properties props) {
        super(props);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().getBlockState(context.getClickedPos()).is(Blocks.CAULDRON)){
            if(context.getLevel().isClientSide){
                Helper.drawParticleRing(context.getLevel(), Registration.RUNE_PARTICLE, context.getClickedPos(), 0.7, 0.9, 50);
            }else{
                context.getLevel().setBlock(context.getClickedPos(), Registration.CRUCIBLE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS, 1.0F, 0.8F);
            }
            if(!context.getPlayer().isCreative())
                context.getPlayer().setItemInHand(context.getHand(), Items.PAPER.getDefaultInstance());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
