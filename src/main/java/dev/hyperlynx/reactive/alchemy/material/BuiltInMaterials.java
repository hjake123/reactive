package dev.hyperlynx.reactive.alchemy.material;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class BuiltInMaterials {
    public static void addToMap(Map<ResourceLocation, Material> map) {
        addMaterial(map, "example_salt", "Example Material", Map.of(
                MaterialProperties.MODEL_NAME.get(), MaterialModel.CIRCLES.getSerializedName(),
                MaterialProperties.COLOR.get(), new Color(0x7A5BB5)
        ));
        addMaterial(map, "example_adept_salt", "Example Material", Map.of(
                MaterialProperties.MODEL_NAME.get(), MaterialModel.SQUARES.getSerializedName(),
                MaterialProperties.COLOR.get(), new Color(0x60F5FA)
        ));
        addMaterial(map, "example_creation_salt", "Example Material", Map.of(
                MaterialProperties.MODEL_NAME.get(), MaterialModel.STATIC.getSerializedName(),
                MaterialProperties.COLOR.get(), new Color(0x118066)
        ));
        addMaterial(map, "example_wool", "Example Dyed Wool", Map.of(
                MaterialProperties.MODEL_NAME.get(), MaterialModel.WOOL.getSerializedName(),
                MaterialProperties.COLOR.get(), new Color(0xf6dab4)
        ));

        // Accept materials from external sources (like e.g. KubeJS integration)
        BuiltInMaterialEvent event = new BuiltInMaterialEvent();
        MinecraftForge.EVENT_BUS.post(event);
        map.putAll(event.getExternalMaterials());
    }

    public static Map<ResourceLocation, Material> generate() {
        Map<ResourceLocation, Material> mats = new HashMap<>();
        addToMap(mats);
        return mats;
    }

    private static void addMaterial(Map<ResourceLocation, Material> map, String id, String name, Map<MaterialProperty<?>, Object> properties) {
        map.put(ReactiveMod.location(id), new Material(properties, name));
    }

    /**
     * This event is fired on both sides after BuiltInMaterials constructs the native built-in materials.
     * You can add new built-in materials using addMaterial().
     */
    public static class BuiltInMaterialEvent extends Event {
        private final Map<ResourceLocation, Material> externally_made_materials = new HashMap<>();
        public void addMaterial(ResourceLocation id, Material material) {
            externally_made_materials.put(id, material);
        }
        private Map<ResourceLocation, Material> getExternalMaterials() {
            return externally_made_materials;
        }
    }
}

