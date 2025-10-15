package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.formula.Formula;
import dev.hyperlynx.reactive.alchemy.material.formula.MaterialFormulaMaps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/// It's MaterialMan's time to shine!
/// Manages the world's Material save data.
/// Materials are stored in a SavedData file and created by crafting a Material Block (or running a command).
/// Each Material has an associated integer ID, which is its position in the list.
public class MaterialMan {
    public static MaterialData data(Level level) {
        if(level instanceof ServerLevel slevel) {
            return Objects.requireNonNull(slevel.getServer().getLevel(ServerLevel.OVERWORLD)).getDataStorage()
                    .computeIfAbsent(new SavedData.Factory<>(() -> MaterialData.empty(), MaterialData::load),
                            "reactive_materials");
        } else if(level != null && level.isClientSide()) {
            return ClientMaterialMan.data();
        }
        ReactiveMod.LOGGER.debug("Tried to fetch data before ServerLevel was available. Level is {}", level);
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
    }

    public static boolean occupied(Level level, ResourceLocation id) {
        return data(level).materials.containsKey(id);
    }

    public static void rename(ServerLevel level, Player player, ResourceLocation id, String name) {
        Material material = data(level).get(id);
        material.setName(name);
        if(!material.wasDiscovered()) {
            material.setDiscoverer(player);
            ReactiveCriterionTriggers.DISCOVER_MATERIAL.get().trigger((ServerPlayer) player);
        }
        data(level).setDirty();
    }

    /// Creates or gets a [Material] based on the formula provided
    /// and returns the ResourceLocation of that material, to be set onto an item or block.
    public static ResourceLocation createOrFetchByFormula(Level level, @NotNull Formula formula) {
        for(Map.Entry<ResourceLocation, Material> existing_material : data(level).materials.entrySet()) {
            if(existing_material.getValue().formulaMatches(formula)) {
                return existing_material.getKey();
            }
        }
        // If we reach this point, we're crafting a completely new material!

        // Decide on the identifier for the new material.
        // Since they're being made automatically, call it "auto:#".
        int index = data(level).materials.size();
        ResourceLocation new_material_id = ResourceLocation.fromNamespaceAndPath("auto", "" + index);
        while(occupied(level, new_material_id)) {
            // Ideally the above index will never already be taken, but in case it is for some reason we need to increment.
            index++;
            new_material_id = ResourceLocation.fromNamespaceAndPath("auto", "" + index);
        }

        addMaterial(level, new_material_id, generateMaterial(formula));
        ReactiveMod.LOGGER.info("Created new material {}", new_material_id);
        return new_material_id;
    }

    /// Just makes a Material without adding it to the world.
    public static @NotNull Material generateMaterial(@NotNull Formula formula) {
        // Retrieve the base item's yield entry
        YieldEntry yield  = MaterialFormulaMaps.BASE_YIELDS.get(formula.base_material().location());
        if(yield == null) {
            throw new IllegalStateException("Tried to make a material using a base (" + formula.base_material().location()  +") with no defined yield! This shouldn't have been possible...");
        }

        // Construct and add the new material
        return new Material(generateProperties(formula.powers(), yield), "", formula.copy());
    }

    private static Map<MaterialProperty<?>, Object> generateProperties(Map<Power, Integer> input_powers, YieldEntry yield_entry) {
        Map<MaterialProperty<?>, Object> properties = new HashMap<>();

        // If this base only yields cosmetic materials, just set the color and model name and return.
        if(yield_entry.wool()) {
            properties.put(MaterialProperties.COLOR.get(), MaterialProperties.COLOR.get().instance(input_powers));
            properties.put(MaterialProperties.MODEL_NAME.get(), yield_entry.default_model());
            if(MaterialProperties.LIGHT.get().requirementsMet(input_powers)) {
                properties.put(MaterialProperties.LIGHT.get(), MaterialProperties.LIGHT.get().instance(input_powers));
            }
            // Since it's wool, it should be flammable,
            properties.put(MaterialProperties.FLAMMABILITY.get(), 30);
            return properties;
        }

        // Apply the effect multiplier from the base item
        Map<Power, Integer> adjusted_input_powers = new HashMap<>(input_powers);
        adjusted_input_powers.replaceAll((p, v) -> (int) (adjusted_input_powers.get(p) * yield_entry.power_effect_multiplier()));

        // Assign the properties
        for(MaterialProperty<?> property : MaterialProperties.PROPERTY_SUPPLIER.get().getValues().stream().toList()) {
            // Check against the formula's actual inputs
            if(property.requirementsMet(adjusted_input_powers)) {
                // Apply properties based on the adjusted powers
                properties.put(property, property.instance(adjusted_input_powers));
            }
        }

        // Use the default model for this yield entry if necessary
        if(properties.get(MaterialProperties.MODEL_NAME.get()).equals("default")) {
            properties.put(MaterialProperties.MODEL_NAME.get(), yield_entry.default_model());
        }

        return properties;
    }
}
