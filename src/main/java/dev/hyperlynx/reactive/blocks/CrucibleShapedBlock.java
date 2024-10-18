package dev.hyperlynx.reactive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CrucibleShapedBlock extends Block {
    // Copied from the model itself. There's probably a better way!
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(3, 1, 3, 13, 2, 13), Shapes.or(Block.box(13, 2, 3,14, 10, 13), Shapes.or(Block.box(2, 2, 3,3, 10, 13), Shapes.or(Block.box(3, 2, 2, 13, 10, 3), Shapes.or(Block.box(3, 2, 13,13, 10, 14))))));

    public CrucibleShapedBlock(Properties props) {
        super(props);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}
