package dev.hyperlynx.reactive.registration;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperty;
import dev.hyperlynx.reactive.alchemy.material.formula.FormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.PropertyFormulaRequirements;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber
public class ReactiveDataMaps {
    // TODO Advanced data map?
    public static final DataMapType<MaterialProperty<?>, PropertyFormulaRequirements> PROPERTY_FORMULA_MAP = DataMapType.builder(
            ReactiveMod.location("formula_requirements"),
            MaterialProperties.MATERIAL_PROPERTY_REGISTRY_KEY,
            PropertyFormulaRequirements.CODEC
    ).build();

    public static final DataMapType<MaterialProperty<?>, FormulaOutcome> FORMULA_OUTCOME_MAP = DataMapType.builder(
            ReactiveMod.location("formula_outcomes"),
            MaterialProperties.MATERIAL_PROPERTY_REGISTRY_KEY,
            FormulaOutcome.CODEC
    ).build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(PROPERTY_FORMULA_MAP);
        event.register(FORMULA_OUTCOME_MAP);
    }
}
