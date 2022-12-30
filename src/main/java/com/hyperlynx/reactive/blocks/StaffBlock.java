package com.hyperlynx.reactive.blocks;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.be.StaffBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StaffBlock extends BaseStaffBlock implements EntityBlock {
    public StaffBlock(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StaffBlockEntity(pos, state);
    }

    // Drop a staff BlockItem with applied durability when the staff is removed.
    // This negates the need for loot tables to be applied to the staves. DO NOT add staff loot tables, it will dupe items.
    @Override
    public void playerDestroy(Level level, Player destroyer, BlockPos pos, BlockState state, @Nullable BlockEntity staff_entity, ItemStack tool) {
        super.playerDestroy(level, destroyer, pos, state, staff_entity, tool);
        ItemStack drop_stack = this.asItem().getDefaultInstance();
        if(staff_entity != null)
            drop_stack.setDamageValue(((StaffBlockEntity) staff_entity).durability);
        popResource(level, pos, drop_stack);
    }
}
