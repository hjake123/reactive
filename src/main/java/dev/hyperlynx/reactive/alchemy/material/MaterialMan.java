package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Map;

/// It's MaterialMan's time to shine!
/// Manages the world's Material save data.
/// Materials are stored in a SavedData file and created by crafting a Material Block (or running a command).
/// Each Material has an associated integer ID, which is its position in the list.
public class MaterialMan {
    public static MaterialData data(Level level) {
        if(level instanceof ServerLevel slevel) {
            return slevel.getServer().getLevel(ServerLevel.OVERWORLD).getDataStorage()
                    .computeIfAbsent(new SavedData.Factory<>(() -> MaterialData.empty().addBuiltIns(slevel), MaterialData::load),
                            "reactive_materials");
        } else if(level != null && level.isClientSide()) {
            return ClientMaterialMan.data();
        }
        ReactiveMod.LOGGER.debug("Tried to fetch data before ServerLevel was available. Level is {}", level.toString());
        return MaterialData.empty();
    }

    public static Material fetch(Level level, ResourceLocation id) {
        return data(level).get(id);
    }

    public static void addMaterial(Level level, ResourceLocation id, Material material) {
        data(level).addMaterial(id, material);
    }

    public static Map<ResourceLocation, Material> getAll(Level level) {
        return data(level).materials;
    }

    public static void remove(Level level, ResourceLocation id) {
        data(level).setToEmpty(id);
    }

    public static void reset(ServerLevel level) {
        var data = data(level);
        data.reset();
        data.addBuiltIns(level);
    }

    public static boolean occupied(Level level, ResourceLocation id) {
        return data(level).materials.containsKey(id);
    }

    public static void rename(ServerLevel level, ResourceLocation id, String name) {
        data(level).get(id).setName(name);
        data(level).setDirty();
    }
}
