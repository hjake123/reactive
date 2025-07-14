package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.util.LightingMan;
import dev.hyperlynx.reactive.registration.ReactiveBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MaterialBlockEntity extends BlockEntity {
    public static final LightingMan lights = new LightingMan();
    ResourceLocation material_id;
    public int generic_delay = 0;

    public MaterialBlockEntity(BlockPos pos, BlockState blockState) {
        super(ReactiveBlockEntityTypes.MATERIAL.get(), pos, blockState);
    }

    public Material getMaterial() {
        if(material_id == null) {
            return Material.empty();
        }
        return MaterialMan.fetch(level, material_id);
    }

    public void setMaterial(Level level, ResourceLocation material_id) {
        if(MaterialMan.occupied(level, material_id)) {
            this.material_id = material_id;
        } else {
            ReactiveMod.LOGGER.error("Material block tried to take an invalid id {}, which is not yet occupied.", material_id);
        }
    }

    public boolean hasNoValidMaterial() {
        return this.material_id == null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        material_id = ResourceLocation.tryParse(tag.getString("material_id"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(material_id == null) {
            return;
        }
        tag.put("material_id", StringTag.valueOf(material_id.toString()));
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

    public ResourceLocation getMaterialId() {
        return material_id;
    }
}
