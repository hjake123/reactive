package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.registration.ReactiveBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MaterialBlockEntity extends BlockEntity {
    int material_id;

    public MaterialBlockEntity(BlockPos pos, BlockState blockState) {
        super(ReactiveBlockEntityTypes.MATERIAL.get(), pos, blockState);
    }

    public Material getMaterial() {
        return MaterialMan.fetch(material_id);
    }
}
