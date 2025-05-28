package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;

public class MaterialData extends SavedData {
    protected final List<Material> materials;

    public static StreamCodec<RegistryFriendlyByteBuf, MaterialData> STREAM_CODEC = StreamCodec.composite(
            Material.STREAM_CODEC.apply(ByteBufCodecs.list()), MaterialData::materials,
            MaterialData::new
    );

    public static MaterialData empty() {
        return new MaterialData(new ArrayList<>());
    }

    MaterialData(List<Material> materials) {
        this.materials = materials;
    }

    private List<Material> materials() {
        return materials;
    }

    public Material get(int index) {
        if (index >= materials.size() || index < 0) {
            ReactiveMod.LOGGER.error("Invalid material index {}", index);
            return Material.empty();
        }
        return materials.get(index);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Material material : materials) {
            list.add(Material.CODEC.encode(material, NbtOps.INSTANCE, null).getOrThrow(error -> new RuntimeException("Failed to save material type: " + error)));
        }
        tag.put("materials", list);
        return tag;
    }

    public static MaterialData load(CompoundTag full_tag, HolderLookup.Provider registries) {
        var list = full_tag.getList("materials", ListTag.TAG_COMPOUND);
        List<Material> materials = new ArrayList<>();
        for (Tag tag : list) {
            materials.add(Material.CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst());
        }
        return new MaterialData(materials);
    }

    public void addMaterial(Material material) {
        materials.add(material);
        setDirty();
    }

    public void setToEmpty(int index) {
        materials.set(index, Material.empty());
        setDirty();
    }

    public void reset() {
        materials.clear();
        setDirty();
    }

    @Override
    public void setDirty() {
        super.setDirty();

    }
}
