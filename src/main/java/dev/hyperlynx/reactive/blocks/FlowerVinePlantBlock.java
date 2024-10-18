package dev.hyperlynx.reactive.blocks;

import dev.hyperlynx.reactive.Registration;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FlowerVinePlantBlock extends GrowingPlantBodyBlock {
    public static final MapCodec<FlowerVinePlantBlock> CODEC = simpleCodec(FlowerVinePlantBlock::new);

    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public FlowerVinePlantBlock(Properties props) {
        super(props, Direction.UP, SHAPE, false);
    }

    @Override
    protected @NotNull GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) Registration.FLOWER_VINES.get();
    }


    @Override
    protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
        return CODEC;
    }
}
