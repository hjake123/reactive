package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.net.MaterialDataSyncPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
    private final List<ResourceLocation> datapack_material_ids; // Remove these when reloading materials

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, Material.STREAM_CODEC), MaterialData::materials,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), MaterialData::datapack_material_ids,
            MaterialData::new
    );

    public MaterialData(MaterialData data) {
        materials = new HashMap<>(data.materials);
        datapack_material_ids = new ArrayList<>(data.datapack_material_ids);
    }

    public MaterialData addBuiltIns(ServerLevel level) {
        var optional_registry = level.registryAccess().registry(BuiltInMaterials.KEY);
        if(optional_registry.isEmpty()) {
            ReactiveMod.LOGGER.error("No built in material registry was defined, so none will be loaded.");
            return this;
        }
        ReactiveMod.LOGGER.info("Removing existing datapack materials");
        for(ResourceLocation existing_builtin_id : datapack_material_ids) {
            Material stub = Material.empty();
            stub.setName(Component.translatable("text.reactive.datapack_material_removed").getString());
            materials.put(existing_builtin_id, stub);
        }
        datapack_material_ids.clear();
        for(Map.Entry<ResourceKey<Material>, Material> material_entry : optional_registry.get().entrySet()) {
            materials.put(material_entry.getKey().location(), material_entry.getValue());
            datapack_material_ids.add(material_entry.getKey().location());
            ReactiveMod.LOGGER.info("Adding datapack material {}", material_entry.getKey().location());
        }
        return this;
    }

    public static MaterialData empty() {
        return new MaterialData(new HashMap<>(), new ArrayList<>());
    }

    public MaterialData(Map<ResourceLocation, Material> materials, List<ResourceLocation> datapack_material_ids) {
        this.materials = materials;
        this.datapack_material_ids = datapack_material_ids;
    }

    private Map<ResourceLocation, Material> materials() {
        return materials;
    }

    private List<ResourceLocation> datapack_material_ids() {
        return datapack_material_ids;
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
        ListTag datapack_ids = new ListTag();
        for(ResourceLocation id : datapack_material_ids) {
            datapack_ids.add(StringTag.valueOf(id.toString()));
        }
        tag.put("datapack_material_ids", datapack_ids);
        return tag;
    }

    public static MaterialData load(CompoundTag full_tag, HolderLookup.Provider ignored) {
        var list = full_tag.getList("materials", ListTag.TAG_COMPOUND);
        Map<ResourceLocation, Material> materials = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry_tag = list.getCompound(i);
            Material material = Material.CODEC.decode(NbtOps.INSTANCE, entry_tag.getCompound("material")).getOrThrow().getFirst();
            validate(material);
            ResourceLocation id = ResourceLocation.parse(entry_tag.getString("id"));
            materials.put(id, material);
        }
        List<ResourceLocation> datapack_ids = new ArrayList<>();
        var dpids = full_tag.getList("datapack_material_ids", ListTag.TAG_STRING);
        for(int i = 0; i < list.size(); i++) {
            datapack_ids.add(ResourceLocation.parse(dpids.getString(i)));
        }

        return new MaterialData(materials, datapack_ids);
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
        Material stub = Material.empty();
        stub.setName(Component.translatable("text.reactive.material_removed").getString());
        materials.put(id, stub);
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

}
