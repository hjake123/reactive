package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public static void rename(ServerLevel level, Player player, ResourceLocation id, String name) {
        Material material = data(level).get(id);
        material.setName(name);
        if(!material.wasDiscovered()) {
            material.setDiscoverer(player);
        }
        data(level).setDirty();
    }

    /// Creates or gets a Material based on the formula provided
    /// and returns the ResourceLocation of that material, to be set onto an item or block.
    public static ResourceLocation createOrFetchByFormula(Level level, @NotNull Map<Power, Integer> formula) {
        for(Map.Entry<ResourceLocation, Material> existing_material : data(level).materials.entrySet()) {
            if(existing_material.getValue().formulaMatches(formula)) {
                return existing_material.getKey();
            }
        }
        // If we reach this point, we're crafting a completely new material!

        // Decide on the identifier for the new material.
        // Since they're being made automatically, call it "reactive:auto#".
        int index = data(level).materials.size();
        ResourceLocation new_material_id = ReactiveMod.location("auto" + index);
        while(occupied(level, new_material_id)) {
            // Ideally the above index will never already be taken, but in case it is for some reason we need to increment.
            index++;
            new_material_id = ReactiveMod.location("auto" + index);
        }

        // Decide on the properties of the new material
        Map<MaterialProperty<?>, Object> properties = new HashMap<>();
        properties.put(MaterialProperties.MODEL_NAME.get(), MaterialModel.SALT.getSerializedName());
        for(MaterialProperty<?> property : MaterialProperties.PROPERTY_REGISTRY.stream().toList()) {
            if(property.requirementsMet(formula)) {
                properties.put(property, property.instance(formula));
            }
        }

        // Construct and add the new material
        Material new_material = new Material(properties, "", Optional.of(new HashMap<>(formula)));
        addMaterial(level, new_material_id, new_material);

        return new_material_id;
    }
}
