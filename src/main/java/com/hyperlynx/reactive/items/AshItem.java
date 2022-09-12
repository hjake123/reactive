package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import org.jetbrains.annotations.NotNull;

public class AshItem extends BoneMealItem {
    public AshItem(Properties p_41383_) {
        super(p_41383_);
    }

    // Ash will, about 1/5 of the time, have the same effects as bonemeal.
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().random.nextFloat() < 0.4)
            return super.useOn(context);
        else{
            context.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
    }
}
