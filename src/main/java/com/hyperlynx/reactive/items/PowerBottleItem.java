package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import java.util.Objects;

public class PowerBottleItem extends Item {
    public PowerBottleItem(Properties props) {
        super(props);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return Registration.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!context.getLevel().getBlockState(context.getClickedPos()).is(Registration.CRUCIBLE.get())){
            return InteractionResult.PASS;
        }

        CrucibleBlockEntity crucible = (CrucibleBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
        if(crucible == null) {
            return InteractionResult.PASS;
        }

        boolean changed = false;
        for(Power p : Powers.POWER_SUPPLIER.get()){
            if(p.matchesBottle(context.getItemInHand())){
                if(crucible.addPower(p, WorldSpecificValues.BOTTLE_RETURN.get())) {
                    if(context.getItemInHand().is(Registration.WARP_BOTTLE.get()) && WarpBottleItem.isRiftBottle(context.getItemInHand())){
                        crucible.enderRiftStrength = 2000;
                    }
                    if (context.getItemInHand().getCount() == 1) {
                        Objects.requireNonNull(context.getPlayer()).setItemInHand(context.getHand(), Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                    }
                    else {
                        Objects.requireNonNull(context.getPlayer()).getItemInHand(context.getHand()).shrink(1);
                        context.getPlayer().addItem(Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                    }
                    changed = true;
                }
            }
        }

        if(changed){
            crucible.setDirty();
            crucible.getLevel().playSound(null, crucible.getBlockPos(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1F, 0.65F+(crucible.getLevel().getRandom().nextFloat()/5));
        }

        return InteractionResult.SUCCESS;
    }

}
