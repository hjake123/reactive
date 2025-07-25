package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.alchemy.material.BuiltInMaterials;
import net.minecraft.core.RegistrySetBuilder;

public class BuiltInMaterialGenerator {
    public static RegistrySetBuilder get() {
        return new RegistrySetBuilder().add(
                BuiltInMaterials.KEY,
                bootstrap -> {
                    // TODO: Author some reasonable materials
//                    addMaterial(bootstrap, "firestone", "Firestone", Map.of(
//                            MaterialProperties.MODEL_NAME.get(), MaterialModel.CRACKED.getSerializedName(),
//                            MaterialProperties.COLOR.get(), new Color(0xFF5500),
//                            MaterialProperties.LIGHT.get(), 15,
//                            MaterialProperties.FIRE_SOURCE.get(), Unit.INSTANCE,
//                            MaterialProperties.MAGMA_STEP.get(), Unit.INSTANCE
//                    ), Map.of(Powers.BLAZE_POWER.get(), 1600));
//                    addMaterial(bootstrap, "slipslime", "Slip Slime", Map.of(
//                            MaterialProperties.MODEL_NAME.get(), MaterialModel.SMOOTH.getSerializedName(),
//                            MaterialProperties.COLOR.get(), new Color(0x22cc22),
//                            MaterialProperties.REDSTONE.get(), 15,
//                            MaterialProperties.FRICTION.get(), 0.9F
//                    ));
                }
        );
    }
}
