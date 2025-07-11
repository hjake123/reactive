package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.net.MaterialDataSyncPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class MaterialData extends SavedData {
    protected final Map<ResourceLocation, Material> materials;

    public static StreamCodec<RegistryFriendlyByteBuf, MaterialData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, Material.STREAM_CODEC), MaterialData::materials,
            MaterialData::new
    );

    public MaterialData(MaterialData data) {
        materials = new HashMap<>(data.materials);
    }

    public MaterialData addBuiltIns(ServerLevel level) {
        for(Map.Entry<ResourceKey<Material>, Material> material_entry : level.registryAccess().registry(BuiltInMaterials.KEY).get().entrySet()) {
            addMaterial(material_entry.getKey().location(), material_entry.getValue());
        }
        return this;
    }

    public static MaterialData empty() {
        return new MaterialData(new HashMap<>());
    }

    public MaterialData(Map<ResourceLocation, Material> materials) {
        this.materials = materials;
    }

    private Map<ResourceLocation, Material> materials() {
        return materials;
    }

    public Material get(ResourceLocation id) {
        if (materials.containsKey(id)) {
            return materials.get(id);
        }
        ReactiveMod.LOGGER.error("Invalid material identifier {}", id);
        return Material.empty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<ResourceLocation, Material> material_entry : materials.entrySet()) {
            CompoundTag entry_tag = new CompoundTag();
            entry_tag.putString("id", material_entry.getKey().toString());
            entry_tag.put("material", Material.CODEC.encode(material_entry.getValue(), NbtOps.INSTANCE, null).getOrThrow(error -> new RuntimeException("Failed to save material type: " + error)));
            list.add(entry_tag);
        }
        tag.put("materials", list);
        return tag;
    }

    public static MaterialData load(CompoundTag full_tag, HolderLookup.Provider registries) {
        var list = full_tag.getList("materials", ListTag.TAG_COMPOUND);
        Map<ResourceLocation, Material> materials = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry_tag = list.getCompound(i);
            Material material = Material.CODEC.decode(NbtOps.INSTANCE, entry_tag.getCompound("material")).getOrThrow().getFirst();
            validate(material);
            ResourceLocation id = ResourceLocation.parse(entry_tag.getString("id"));
            materials.put(id, material);
        }
        return new MaterialData(materials);
    }

    private static void validate(Material material) {
        if(!material.has(MaterialProperties.MODEL_NAME.get()) || !MaterialModel.isNameValid(material.get(MaterialProperties.MODEL_NAME.get()))) {
            ReactiveMod.LOGGER.error("Material has an invalid or missing model name {}", material.getOrDefault(MaterialProperties.MODEL_NAME.get(), "<null>"));
            material.set(MaterialProperties.MODEL_NAME.get(), "salt");
        }
    }

    public void addMaterial(ResourceLocation id, Material material) {
        materials.put(id, material);
        setDirty();
    }

    public void setToEmpty(ResourceLocation id) {
        materials.put(id, dev.hyperlynx.reactive.alchemy.material.Material.empty());
        setDirty();
    }

    public void reset() {
        materials.clear();
        setDirty();
    }

    @Override
    public void setDirty() {
        super.setDirty();
        PacketDistributor.sendToAllPlayers(new MaterialDataSyncPayload(new MaterialData(this)));
    }

    public Collection<ResourceLocation> getKeys() {
        return materials.keySet();
    }
}
