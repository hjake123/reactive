package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.Objects;

public class PowerBottleItem extends Item {
    public final static int BOTTLE_COST = 600;

    public PowerBottleItem(Properties props) {
        super(props);
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return Registration.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseItemBehavior() {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack dispense(BlockSource source, ItemStack stack) {
            BlockPos target = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            if(!(source.getLevel().getBlockState(target).getBlock() instanceof CrucibleBlock)){
                return defaultDispenseItemBehavior.dispense(source, stack);
            }

            CrucibleBlockEntity crucible = (CrucibleBlockEntity) source.getLevel().getBlockEntity(target);
            if(crucible == null) {
                return defaultDispenseItemBehavior.dispense(source, stack);
            }

            boolean changed = false;
            for(Power p : Powers.POWER_SUPPLIER.get()){
                if(p.matchesBottle(stack)){
                    if(crucible.addPower(p, WorldSpecificValues.BOTTLE_RETURN.get())) {
                        if(stack.is(Registration.WARP_BOTTLE.get()) && WarpBottleItem.isRiftBottle(stack)){
                            crucible.enderRiftStrength = 2000;
                        }
                        stack.shrink(1);
                        ItemEntity quartz_bottle_drop = new ItemEntity(source.getLevel(), target.getX()+0.5, target.getY()+0.6, target.getZ()+0.5, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                        source.getLevel().addFreshEntity(quartz_bottle_drop);
                        changed = true;
                    }
                }
            }

            if(changed){
                crucible.setDirty();
                crucible.getLevel().playSound(null, crucible.getBlockPos(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1F, 0.65F+(crucible.getLevel().getRandom().nextFloat()/5));
            }
            return stack;
        }
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof CrucibleBlock)){
            return super.useOn(context);
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

    public static void tryEmptyPowerBottle(ItemEntity e, CrucibleBlockEntity c){
        boolean changed = false;
        for(Power p : Powers.POWER_SUPPLIER.get()){
            if(p.matchesBottle(e.getItem())){
                if(c.addPower(p, WorldSpecificValues.BOTTLE_RETURN.get())) {
                    if(e.getItem().is(Registration.WARP_BOTTLE.get()) && WarpBottleItem.isRiftBottle(e.getItem())){
                        c.enderRiftStrength = 2000;
                    }
                    if (e.getItem().getCount() == 1) {
                        e.setItem(Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                    }
                    else {
                        e.getItem().shrink(1);
                        ItemEntity empty_bottle = new ItemEntity(c.getLevel(), e.getX(), e.getY(), e.getZ(), Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                        e.level().addFreshEntity(empty_bottle);
                    }
                    changed = true;
                }
            }
        }

        if(changed){
            c.setDirty();
            c.getLevel().playSound(null, c.getBlockPos(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1F, 0.65F+(c.getLevel().getRandom().nextFloat()/5));
        }
    }

}
