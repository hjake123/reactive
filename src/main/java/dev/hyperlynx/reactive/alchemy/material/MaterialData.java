package dev.hyperlynx.reactive.alchemy.material;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.util.NBTSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialData extends SavedData {
    protected final Map<ResourceLocation, Material> materials;
    private final List<ResourceLocation> datapack_material_ids; // Remove these when reloading materials

    /// Bump this when there's a change in the material data format that would cause issues if not dealt with.
    public static final int CURRENT_VERSION = 12011;
    /*
    Version history:
    1 - Initial version for 1.21.1; not compatible
    12011 - Initial version for the backport
     */

    private static final NBTSerializer<MaterialData> SERIALIZER_V1 = new NBTSerializer<>() {
        @Override
        public CompoundTag encode(MaterialData data) {
            return null;
        }

        @Override
        public @Nullable MaterialData decode(Tag input) {
            return null;
        }
    };

    public static final Map<Integer, NBTSerializer<MaterialData>> CODECS_BY_VERSION = Map.of(
            1201, SERIALIZER_V1
    );

    public MaterialData(Map<ResourceLocation, Material> materials, List<ResourceLocation> datapack_material_ids) {
        this.materials = new HashMap<>(materials);
        this.datapack_material_ids = new ArrayList<>(datapack_material_ids);
    }

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
        return Material.empty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag saved_tag = SERIALIZER_V1.encode(this, tag);
        saved_tag.put("version", IntTag.valueOf(CURRENT_VERSION));
        return saved_tag;
    }

    public static MaterialData load(CompoundTag tag, HolderLookup.Provider ignoredprovider) {
        int version = CURRENT_VERSION;
        if(tag.contains("version", Tag.TAG_INT)) {
            version = tag.getInt("version");
        }
        if(version != CURRENT_VERSION) {
            ReactiveMod.LOGGER.info("Attempting to load material data with mismatched version {} (current is {})", version, CURRENT_VERSION);
            if(!CODECS_BY_VERSION.containsKey(version)) {
                ReactiveMod.LOGGER.error("No serializer for this version was found, attempting load with most recent serializer.");
            }
        }
        NBTSerializer<MaterialData> serializer = CODECS_BY_VERSION.getOrDefault(version, SERIALIZER_V1);

        return serializer.decode(tag);
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

    public int datapackIdCount() {
        return datapack_material_ids.size();
    }
}
