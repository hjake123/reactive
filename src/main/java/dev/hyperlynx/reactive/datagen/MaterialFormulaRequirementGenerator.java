package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.PropertyFormulaRequirement;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MaterialFormulaRequirementGenerator extends DataMapProvider {
    protected MaterialFormulaRequirementGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(ReactiveDataMaps.PROPERTY_FORMULA_MAP)
                .add(MaterialProperties.MAGMA_STEP, new PropertyFormulaRequirement(List.of(
                        new PropertyFormulaRequirement.Part(Powers.BLAZE_POWER.getId(),
                                500, 1000,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.FIRE_SOURCE, new PropertyFormulaRequirement(List.of(
                        new PropertyFormulaRequirement.Part(Powers.BLAZE_POWER.getId(),
                                1, 200,
                                Optional.empty(), Optional.empty())
                )), false);
    }
}
