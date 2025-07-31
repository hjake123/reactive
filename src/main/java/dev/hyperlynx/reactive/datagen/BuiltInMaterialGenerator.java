package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.*;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public class BuiltInMaterialGenerator {
    public static RegistrySetBuilder get() {
        return new RegistrySetBuilder().add(
                BuiltInMaterials.KEY,
                bootstrap -> {
                    addMaterial(bootstrap, "example_salt", "Example Material", Map.of(
                            MaterialProperties.MODEL_NAME.get(), MaterialModel.SALT.getSerializedName(),
                            MaterialProperties.COLOR.get(), new Color(0x7A5BB5)
                    ));
                    addMaterial(bootstrap, "example_adept_salt", "Example Material", Map.of(
                            MaterialProperties.MODEL_NAME.get(), MaterialModel.SQUARES.getSerializedName(),
                            MaterialProperties.COLOR.get(), new Color(0x60F5FA)
                    ));
                    addMaterial(bootstrap, "example_creation_salt", "Example Material", Map.of(
                            MaterialProperties.MODEL_NAME.get(), MaterialModel.STATIC.getSerializedName(),
                            MaterialProperties.COLOR.get(), new Color(0x118066)
                    ));
                    addMaterial(bootstrap, "example_wool", "Example Material", Map.of(
                            MaterialProperties.MODEL_NAME.get(), MaterialModel.WOOL.getSerializedName(),
                            MaterialProperties.COLOR.get(), new Color(0x9800FF)
                    ));
                }
        );
    }

    private static void addMaterial(BootstrapContext<Material> bootstrap, String id, String name, Map<MaterialProperty<?>, Object> properties) {
        bootstrap.register(ResourceKey.create(BuiltInMaterials.KEY, ReactiveMod.location(id)),
                new Material(properties, name
        ));
    }
}
