package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;

/// It's MaterialMan's time to shine!
/// Manages the world's Material save data.
/// Materials are stored in a SavedData file and created by crafting a Material Block (or running a command).
/// Each Material has an associated integer ID, which is its position in the list.
public class MaterialMan {
    private static MaterialData data(ServerLevel level) {
        return level.getServer().getLevel(ServerLevel.OVERWORLD).getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(MaterialData::empty, MaterialData::load),
                "reactive_materials");
    }

    public static Material fetch(ServerLevel level, int materialId) {
        return data(level).materials.get(materialId);
    }

    public static void addMaterial(ServerLevel level, Material material) {
        data(level).addMaterial(material);
    }

    public static List<Material> getAll(ServerLevel level) {
        return data(level).materials;
    }

    private static class MaterialData extends SavedData {
        private final List<Material> materials;

        public static MaterialData empty(){
            return new MaterialData(new ArrayList<>());
        }

        MaterialData(List<Material> materials) {
            this.materials = materials;
        }

        public Material get(int index) {
            if(index >= materials.size()) {
                ReactiveMod.LOGGER.error("Invalid material index {}", index);
                return Material.empty();
            }
            return materials.get(index);
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            ListTag list = new ListTag();
            for(Material material : materials) {
                list.add(Material.CODEC.encode(material, NbtOps.INSTANCE, null).getOrThrow(error -> new RuntimeException("Failed to save material type: " + error)));
            }
            tag.put("materials", list);
            return tag;
        }

        public static MaterialData load(CompoundTag full_tag, HolderLookup.Provider registries) {
            var list = full_tag.getList("materials", ListTag.TAG_COMPOUND);
            List<Material> materials = new ArrayList<>();
            for(Tag tag : list) {
                materials.add(Material.CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst());
            }
            return new MaterialData(materials);
        }

        public void addMaterial(Material material) {
            materials.add(material);
            setDirty();
        }
    }
}
