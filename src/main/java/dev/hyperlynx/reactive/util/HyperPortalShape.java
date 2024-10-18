package dev.hyperlynx.reactive.util;

import javax.annotation.Nullable;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;

// Verbatim clone of PortalShape, but it pretends SolidPortalBlocks are empty space.
// Only other way to do this was a mixin or something so this seems preferable.
public class HyperPortalShape {

    // The only edits.
    private static boolean isEmpty(BlockState state) {
        return state.isAir() || state.is(BlockTags.FIRE) || state.is(Blocks.NETHER_PORTAL) || state.is(Registration.SOLID_PORTAL.get());
    }

    public void createSolidPortalBlocks() {
        BlockState blockstate = Registration.SOLID_PORTAL.get().defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((p_77725_) -> {
            this.level.setBlock(p_77725_, blockstate, 18);
        });
    }

    public boolean isValid() {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    // Everything else matches PortalShape (with some removals).
    private static final BlockBehaviour.StatePredicate FRAME = IBlockStateExtension::isPortalFrame;
    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int numPortalBlocks;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private final int width;

    public HyperPortalShape(LevelAccessor p_77695_, BlockPos p_77696_, Direction.Axis p_77697_) {
        this.level = p_77695_;
        this.axis = p_77697_;
        this.rightDir = p_77697_ == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.calculateBottomLeft(p_77696_);
        if (this.bottomLeft == null) {
            this.bottomLeft = p_77696_;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.calculateWidth();
            if (this.width > 0) {
                this.height = this.calculateHeight();
            }
        }

    }

    @Nullable
    private BlockPos calculateBottomLeft(BlockPos p_77734_) {
        for(int i = Math.max(this.level.getMinBuildHeight(), p_77734_.getY() - 21); p_77734_.getY() > i && isEmpty(this.level.getBlockState(p_77734_.below())); p_77734_ = p_77734_.below()) {
        }

        Direction direction = this.rightDir.getOpposite();
        int j = this.getDistanceUntilEdgeAboveFrame(p_77734_, direction) - 1;
        return j < 0 ? null : p_77734_.relative(direction, j);
    }

    private int calculateWidth() {
        int i = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private int getDistanceUntilEdgeAboveFrame(BlockPos p_77736_, Direction p_77737_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int i = 0; i <= 21; ++i) {
            blockpos$mutableblockpos.set(p_77736_).move(p_77737_, i);
            BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
            if (!isEmpty(blockstate)) {
                if (FRAME.test(blockstate, this.level, blockpos$mutableblockpos)) {
                    return i;
                }
                break;
            }

            BlockState blockstate1 = this.level.getBlockState(blockpos$mutableblockpos.move(Direction.DOWN));
            if (!FRAME.test(blockstate1, this.level, blockpos$mutableblockpos)) {
                break;
            }
        }

        return 0;
    }

    private int calculateHeight() {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i = this.getDistanceUntilTop(blockpos$mutableblockpos);
        return i >= 3 && i <= 21 && this.hasTopFrame(blockpos$mutableblockpos, i) ? i : 0;
    }

    private boolean hasTopFrame(BlockPos.MutableBlockPos p_77731_, int p_77732_) {
        for(int i = 0; i < this.width; ++i) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = p_77731_.set(this.bottomLeft).move(Direction.UP, p_77732_).move(this.rightDir, i);
            if (!FRAME.test(this.level.getBlockState(blockpos$mutableblockpos), this.level, blockpos$mutableblockpos)) {
                return false;
            }
        }

        return true;
    }

    private int getDistanceUntilTop(BlockPos.MutableBlockPos p_77729_) {
        for(int i = 0; i < 21; ++i) {
            p_77729_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
            if (!FRAME.test(this.level.getBlockState(p_77729_), this.level, p_77729_)) {
                return i;
            }

            p_77729_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
            if (!FRAME.test(this.level.getBlockState(p_77729_), this.level, p_77729_)) {
                return i;
            }

            for(int j = 0; j < this.width; ++j) {
                p_77729_.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState blockstate = this.level.getBlockState(p_77729_);
                if (!isEmpty(blockstate)) {
                    return i;
                }

                if (blockstate.is(Blocks.NETHER_PORTAL)) {
                    ++this.numPortalBlocks;
                }
            }
        }

        return 21;
    }

    public void createPortalBlocks() {
        BlockState blockstate = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((p_77725_) -> {
            this.level.setBlock(p_77725_, blockstate, 18);
        });
    }

    public boolean isComplete() {
        return this.isValid() && this.numPortalBlocks == this.width * this.height;
    }

}
