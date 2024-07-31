package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Like a Crucible Block, but it can be picked up like a Skulker Box!
public class ShulkerCrucibleBlock extends CrucibleBlock{
    public ShulkerCrucibleBlock(Properties p) {
        super(p);
    }

    // Drop a ShulkerCrucibleBlock BlockItem with block entity data saved.
    // This negates the need for loot tables to be applied to this block.
    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CrucibleBlockEntity crucible) {
            if (!level.isClientSide) {
                ItemStack drop_stack = Registration.SHULKER_CRUCIBLE_ITEM.get().getDefaultInstance();
                if(crucible.getTotalPowerLevel() > 0) {
                    crucible.saveToItem(drop_stack, level.registryAccess());
                    drop_stack.set(DataComponents.LORE, new ItemLore(List.of(Component.literal(getItemLabel(crucible)))));
                }else if(state.getValue(FULL)){
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6F, 0.8F);
                }
                ItemEntity drop = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop_stack);
                drop.setDefaultPickUpDelay();
                level.addFreshEntity(drop);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
        return state;
    }

    private static String getItemLabel(CrucibleBlockEntity crucible){
        StringBuilder label = new StringBuilder();
        for(Power p : crucible.getPowerMap().keySet()){
            if(crucible.getPowerLevel(p) > 0) {
                label.append(p.getName()).append(", ");
            }
        }
        return label.substring(0, label.length() - 2);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState new_state, boolean p_60519_) {
        super.onRemoveWithoutEmpty(state, level, pos, new_state, p_60519_);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old_state, boolean p_60570_) {
        BlockEntity recently_placed_be = level.getBlockEntity(pos);
        if(!(recently_placed_be instanceof CrucibleBlockEntity crucible))
            return;

        // Necessary to prevent auto-refilling when emptied.
        if(old_state.getBlock() instanceof ShulkerCrucibleBlock)
            return;

        level.setBlock(pos, state.setValue(FULL, crucible.getTotalPowerLevel() > 0), Block.UPDATE_ALL_IMMEDIATE);
    }
}
