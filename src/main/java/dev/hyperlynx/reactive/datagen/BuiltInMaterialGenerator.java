package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.Material;
import dev.hyperlynx.reactive.alchemy.material.MaterialModel;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperty;
import dev.hyperlynx.reactive.alchemy.material.BuiltInMaterials;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;

import java.util.Map;

public class BuiltInMaterialGenerator {
    public static RegistrySetBuilder get() {
        return new RegistrySetBuilder().add(
                BuiltInMaterials.KEY,
                bootstrap -> {
                    addMaterial(bootstrap, "firestone", "Firestone", Map.of(
                            MaterialProperties.MODEL_NAME.get(), MaterialModel.CRACKED.getSerializedName(),
                            MaterialProperties.COLOR.get(), new Color(0xFF8800),
                            MaterialProperties.LIGHT.get(), 15,
                            MaterialProperties.FIRE_SOURCE.get(), Unit.INSTANCE,
                            MaterialProperties.MAGMA_STEP.get(), Unit.INSTANCE
                    ));
                    addMaterial(bootstrap, "slipslime", "Slip Slime", Map.of(
                            MaterialProperties.MODEL_NAME.get(), MaterialModel.GEL.getSerializedName(),
                            MaterialProperties.COLOR.get(), new Color(0x22cc22),
                            MaterialProperties.REDSTONE.get(), 15,
                            MaterialProperties.FRICTION.get(), 0.1F
                    ));
                }
        );
    }

    private static void addMaterial(BootstrapContext<Material> bootstrap, String path, String name, Map<MaterialProperty<?>, Object> properties) {
        bootstrap.register(
                ResourceKey.create(BuiltInMaterials.KEY, ReactiveMod.location(path)),
                new Material(properties, name)
        );
    }
}
