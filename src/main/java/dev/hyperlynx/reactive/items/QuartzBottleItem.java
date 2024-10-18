package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.special.SpecialCaseMan;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

public class QuartzBottleItem extends Item {
    public QuartzBottleItem(Properties props) {
        super(props);
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseItemBehavior() {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        @Override
        public @NotNull ItemStack dispense(BlockSource source, @NotNull ItemStack stack) {
            BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            if(!(source.level().getBlockState(target).getBlock() instanceof CrucibleBlock)){
                return defaultDispenseItemBehavior.dispense(source, stack);
            }

            CrucibleBlockEntity crucible = (CrucibleBlockEntity) source.level().getBlockEntity(target);
            if(crucible == null) {
                return defaultDispenseItemBehavior.dispense(source, stack);
            }

            for(Power p : crucible.getPowerMap().keySet()){
                if(!p.hasBottle())
                    continue;
                if(crucible.getPowerLevel(p) > PowerBottleItem.BOTTLE_COST){
                    crucible.expendPower(p, PowerBottleItem.BOTTLE_COST);
                    stack.shrink(1);
                    source.level().playSound(null, source.pos(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.8F, 1F);
                    ItemEntity power_bottle_drop = new ItemEntity(source.level(), target.getX()+0.5, target.getY()+0.6, target.getZ()+0.5,
                            SpecialCaseMan.checkBottleSpecialCases(crucible, p.getBottle()));
                    source.level().addFreshEntity(power_bottle_drop);
                }
            }
            crucible.setDirty();

            return stack;
        }

    };
}
