package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.formula.*;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/// Remember to runData after updating this!!!
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
                .add(MaterialProperties.ENCHANT_POWER, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.SOUL_POWER.getId(),
                                50, 250,
                                Optional.of(800), Optional.of(1000)),
                        new PropertyFormulaRequirements.Part(Powers.MIND_POWER.getId(),
                                50, 50,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.BLAST_RESISTANCE, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.FRICTION, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.WARP_POWER.getId(),
                                1200, 1600,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.BREAK_STRENGTH, new PropertyFormulaRequirements(List.of()), false)
        ;


        builder(ReactiveDataMaps.FORMULA_OUTCOME_MAP)
                .add(MaterialProperties.LIGHT, List.of(
                        new IntegerZeroToMaxFormulaOutcome(
                                Powers.LIGHT_POWER.getId(),
                                200,
                                1600,
                                15
                )), false)
                .add(MaterialProperties.REDSTONE, List.of(
                        new IntegerZeroToMaxFormulaOutcome(
                                Powers.MIND_POWER.getId(),
                                500,
                                1200,
                                15
                )), false)
                .add(MaterialProperties.FLAMMABILITY, List.of(
                        new IntegerZeroToMaxFormulaOutcome(
                                Powers.MIND_POWER.getId(),
                                700,
                                1600,
                                300
                )), false)
                .add(MaterialProperties.ENCHANT_POWER, List.of(
                        new FloatZeroToMaxFormulaOutcome(
                                Powers.MIND_POWER.getId(),
                                50,
                                1000,
                                2.0F
                )), false)
                .add(MaterialProperties.BLAST_RESISTANCE, List.of(
                        new FloatOneToValueFormulaOutcome(
                                Powers.VITAL_POWER.getId(),
                                250,
                                500,
                                100.0F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.CURSE_POWER.getId(),
                                0,
                                20,
                                0.0F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.BODY_POWER.getId(),
                                0,
                                1200,
                                10.0F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.SOUL_POWER.getId(),
                                500,
                                550,
                                2.0F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.WARP_POWER.getId(),
                                100,
                                550,
                                0.2F)
                    ), false)
                .add(MaterialProperties.FRICTION, List.of(
                        new FloatOneToValueFormulaOutcome(
                                Powers.WARP_POWER.getId(),
                                1200,
                                1600,
                                0.99F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.FLOW_POWER.getId(),
                                10,
                                100,
                                0.98F)
                ), false)
                .add(MaterialProperties.BREAK_STRENGTH, List.of(
                        new FloatOneToValueFormulaOutcome(
                                Powers.VITAL_POWER.getId(),
                                100,
                                110,
                                2.0F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.CURSE_POWER.getId(),
                                0,
                                20,
                                0.01F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.BODY_POWER.getId(),
                                0,
                                1600,
                                5.0F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.VERDANT_POWER.getId(),
                                0,
                                1600,
                                1.25F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.ACID_POWER.getId(),
                                200,
                                250,
                                0.5F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.WARP_POWER.getId(),
                                100,
                                550,
                                0.25F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.MIND_POWER.getId(),
                                777,
                                877,
                                1.05F)
                ), false)
        ;
    }
}
