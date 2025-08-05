package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.blocks.CrucibleBlock;
import dev.hyperlynx.reactive.blocks.DivineSymbolBlock;
import dev.hyperlynx.reactive.blocks.PowerBottleBlock;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PowerBottleItem extends BlockItem implements BasePowerBottle {
    public final static int BOTTLE_COST = 600;

    public PowerBottleItem(Properties props, Block block) {
        super(block, props);
        DispenserBlock.registerBehavior(this, BasePowerBottle.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return ReactiveItems.QUARTZ_BOTTLE.get().getDefaultInstance();
    }

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
            if(context.getPlayer() != null) {
                level.playSound(context.getPlayer(), clicked_pos, this.getPlaceSound(clicked_state, level, clicked_pos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                level.gameEvent(GameEvent.BLOCK_PLACE, clicked_pos, GameEvent.Context.of(context.getPlayer(), clicked_state));
                if (!context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if((context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof DivineSymbolBlock)){
            return InteractionResult.PASS;
        }

        if(!(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof CrucibleBlock)){
            return super.useOn(context);
        }

        return tryAddToCrucible(context);

    }

}
