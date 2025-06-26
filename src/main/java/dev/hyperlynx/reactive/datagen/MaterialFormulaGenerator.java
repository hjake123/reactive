package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.formula.IntegerZeroToMaxFormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.PropertyFormulaRequirements;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MaterialFormulaGenerator extends DataMapProvider {
    protected MaterialFormulaGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(ReactiveDataMaps.PROPERTY_FORMULA_MAP)
                .add(MaterialProperties.MAGMA_STEP, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.BLAZE_POWER.getId(),
                                500, 1000,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.FIRE_SOURCE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.BLAZE_POWER.getId(),
                                1, 200,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.REDSTONE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.MIND_POWER.getId(),
                                500, 500,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.LIGHT, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.LIGHT_POWER.getId(),
                                200, 200,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.FLAMMABILITY, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.VERDANT_POWER.getId(),
                                700, 1200,
                                Optional.empty(), Optional.empty())
                )), false)
        ;


        builder(ReactiveDataMaps.FORMULA_OUTCOME_MAP)
                .add(MaterialProperties.LIGHT, new IntegerZeroToMaxFormulaOutcome(
                        Powers.LIGHT_POWER.getId(),
                        200,
                        1600,
                        15
                ), false)
                .add(MaterialProperties.REDSTONE, new IntegerZeroToMaxFormulaOutcome(
                        Powers.MIND_POWER.getId(),
                        500,
                        1200,
                        15
                ), false)
                .add(MaterialProperties.FLAMMABILITY, new IntegerZeroToMaxFormulaOutcome(
                        Powers.MIND_POWER.getId(),
                        700,
                        1600,
                        300
                ), false)
        ;
    }
}
