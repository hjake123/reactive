package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.registration.ReactiveBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MaterialBlockEntity extends BlockEntity {
    int material_id;

    public MaterialBlockEntity(BlockPos pos, BlockState blockState) {
        super(ReactiveBlockEntityTypes.MATERIAL.get(), pos, blockState);
    }

    public Material getMaterial() {
        if((!(this.level instanceof ServerLevel slevel))) {
            ReactiveMod.LOGGER.error("Trying to fetch material from client side. It won't work, of course...");
            return Material.empty();
        }
        return MaterialMan.fetch(slevel, material_id);
    }
}
