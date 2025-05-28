package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.util.LightingMan;
import dev.hyperlynx.reactive.registration.ReactiveBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MaterialBlockEntity extends BlockEntity {
    public static final LightingMan lights = new LightingMan();
    int material_id = -1;

    public MaterialBlockEntity(BlockPos pos, BlockState blockState) {
        super(ReactiveBlockEntityTypes.MATERIAL.get(), pos, blockState);
    }

    public Material getMaterial() {
        return MaterialMan.fetch(level, material_id);
    }

    public void setMaterial(Level level, int id) {
        if(MaterialMan.occupied(level, id)) {
            this.material_id = id;
        } else {
            ReactiveMod.LOGGER.error("Material block tried to take an invalid id {}, which is not yet occupied.", id);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        material_id = tag.getInt("material_id");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("material_id", IntTag.valueOf(material_id));
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        MaterialBlockEntity.lights.setLightAt(this.getBlockPos(), getMaterial().getOrDefault(MaterialProperties.LIGHT.get(), 0));
    }

    public int getId() {
        return material_id;
    }
}
