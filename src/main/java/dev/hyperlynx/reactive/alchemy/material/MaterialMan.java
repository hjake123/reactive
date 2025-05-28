package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.List;

/// It's MaterialMan's time to shine!
/// Manages the world's Material save data.
/// Materials are stored in a SavedData file and created by crafting a Material Block (or running a command).
/// Each Material has an associated integer ID, which is its position in the list.
public class MaterialMan {
    public static MaterialData data(Level level) {
        if(level instanceof ServerLevel slevel) {
            return slevel.getServer().getLevel(ServerLevel.OVERWORLD).getDataStorage()
                    .computeIfAbsent(new SavedData.Factory<>(MaterialData::empty, MaterialData::load),
                            "reactive_materials");
        }
        return ClientMaterialMan.data();
    }

    public static Material fetch(Level level, int materialId) {
        return data(level).get(materialId);
    }

    public static void addMaterial(Level level, Material material) {
        data(level).addMaterial(material);
    }

    public static List<Material> getAll(Level level) {
        return data(level).materials;
    }

    public static void remove(Level level, int index) {
        data(level).setToEmpty(index);
    }

    public static void reset(Level level) {
        data(level).reset();
    }

    public static boolean occupied(Level level, int id) {
        return data(level).materials.size() > id;
    }

}
