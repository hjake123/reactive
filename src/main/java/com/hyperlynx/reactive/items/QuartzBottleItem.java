package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.SpecialCaseMan;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class QuartzBottleItem extends Item {
    public QuartzBottleItem(Properties props) {
        super(props);
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
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

            for(Power p : crucible.getPowerMap().keySet()){
                if(!p.hasBottle())
                    continue;
                if(crucible.getPowerLevel(p) > PowerBottleItem.BOTTLE_COST){
                    crucible.expendPower(p, PowerBottleItem.BOTTLE_COST);
                    stack.shrink(1);
                    source.getLevel().playSound(null, source.getPos(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1F);
                    ItemEntity power_bottle_drop = new ItemEntity(source.getLevel(), target.getX()+0.5, target.getY()+0.6, target.getZ()+0.5,
                            SpecialCaseMan.checkBottleSpecialCases(crucible, p.getBottle()));
                    source.getLevel().addFreshEntity(power_bottle_drop);
                }
            }
            crucible.setDirty();

            return stack;
        }

    };
}
