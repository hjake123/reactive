package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.blocks.DivineSymbolBlock;
import com.hyperlynx.reactive.blocks.PowerBottleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Objects;

public class PowerBottleItem extends BlockItem {
    public final static int BOTTLE_COST = 600;

    public PowerBottleItem(Properties props, Block block) {
        super(block, props);
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

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if((context.getLevel().getBlockState(context.getClickedPos()).getBlock().equals(this.getBlock()))){
            /*
            Deal with clicking on a Power Bottle with another of the same kind.
             */
            BlockState clicked_state = context.getLevel().getBlockState(context.getClickedPos());
            if(clicked_state.getValue(PowerBottleBlock.BOTTLES) == 3)
                return InteractionResult.PASS;

            Level level = context.getLevel();
            BlockPos clicked_pos = context.getClickedPos();
            level.setBlock(context.getClickedPos(),
                    clicked_state.setValue(PowerBottleBlock.BOTTLES, clicked_state.getValue(PowerBottleBlock.BOTTLES) + 1),
                    Block.UPDATE_CLIENTS);
            SoundType soundtype = clicked_state.getSoundType(level, clicked_pos, context.getPlayer());
            level.playSound(context.getPlayer(), clicked_pos, this.getPlaceSound(clicked_state, level, clicked_pos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, clicked_pos, GameEvent.Context.of(context.getPlayer(), clicked_state));
            if (!context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        if((context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof DivineSymbolBlock)){
            return InteractionResult.PASS;
        }

        if(!(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof CrucibleBlock)){
            return super.useOn(context);
        }

        CrucibleBlockEntity crucible = (CrucibleBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
        if(crucible == null) {
            return InteractionResult.PASS;
        }

        boolean changed = false;
        for(Power p : Powers.POWERS.getRegistry().get()){
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
        for(Power p : Powers.POWERS.getRegistry().get()){
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
