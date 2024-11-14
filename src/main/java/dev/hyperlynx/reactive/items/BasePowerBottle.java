package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBottleInsertContext;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.WorldSpecificValues;
import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.DispenserBlock;

public interface BasePowerBottle {
    DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DispenseItemBehavior() {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack dispense(BlockSource source, ItemStack stack) {
            BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            if(!(source.level().getBlockState(target).getBlock() instanceof CrucibleBlock)){
                return defaultDispenseItemBehavior.dispense(source, stack);
            }

            CrucibleBlockEntity crucible = (CrucibleBlockEntity) source.level().getBlockEntity(target);
            if(crucible == null) {
                return defaultDispenseItemBehavior.dispense(source, stack);
            }

            boolean changed = false;
            for(Power p : Powers.POWERS.getRegistry().get()){
                if(p.matchesBottle(stack)){
                    if(crucible.addPower(p, WorldSpecificValues.BOTTLE_RETURN.get())) {
                        if(stack.is(Registration.WARP_BOTTLE.get()) && WarpBottleItem.isRiftBottle(stack)){
                            crucible.enderRiftStrength = 2000;
                        }
                        stack.shrink(1);
                        ItemEntity quartz_bottle_drop = new ItemEntity(source.level(), target.getX()+0.5, target.getY()+0.6, target.getZ()+0.5, Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
                        source.level().addFreshEntity(quartz_bottle_drop);
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

    default InteractionResult tryAddToCrucible(UseOnContext context){
        CrucibleBlockEntity crucible = (CrucibleBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
        if(crucible == null) {
            return InteractionResult.PASS;
        }

        CrucibleBlockEntity.insertPowerBottle(crucible, new PowerBottleInsertContext(context));
        return InteractionResult.SUCCESS;
    }

}
