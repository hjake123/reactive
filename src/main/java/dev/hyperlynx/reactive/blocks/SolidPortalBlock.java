package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.util.HyperPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SolidPortalBlock extends WaterloggableBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public SolidPortalBlock(Properties prop) {
        super(prop.pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X));

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if(state.getValue(AXIS).equals(Direction.Axis.X))
            return box(6, 0, 0, 10, 16, 16);

        return box(0, 0, 6, 16, 16, 10);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis());
    }


    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity irrelevant, ItemStack stack) {
        HyperPortalShape attempted_portal = new HyperPortalShape(level, pos, state.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        if(attempted_portal.isValid()){
            attempted_portal.createPortalBlocks();
            return;
        }
        super.playerDestroy(level, player, pos, state, irrelevant, stack);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rng) {
        double x = (double)pos.getX() + rng.nextDouble();
        double y = (double)pos.getY() + rng.nextDouble();
        double z = (double)pos.getZ() + rng.nextDouble();

        level.addParticle(ParticleTypes.PORTAL, x, y, z, 0,0,0);
    }
}
