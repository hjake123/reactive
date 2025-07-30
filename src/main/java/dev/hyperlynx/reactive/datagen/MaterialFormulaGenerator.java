package dev.hyperlynx.reactive.datagen;

import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.YieldEntry;
import dev.hyperlynx.reactive.alchemy.material.formula.*;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
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
                                10, 25,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.LIGHT, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.LIGHT_POWER.getId(),
                                200, 200,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.FLAMMABILITY, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.VERDANT_POWER.getId(),
                                120, 240,
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
                .add(MaterialProperties.FRICTION, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.BREAK_STRENGTH, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.MODEL_NAME, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.SELF_DEFENSE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.VITAL_POWER.getId(),
                                1000, 1150,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.WARPING, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.WARP_POWER.getId(),
                                1500, 1510,
                                Optional.empty(), Optional.empty())
                )), false)
                .add(MaterialProperties.REDSTONE_MELTING, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.ACID_POWER.getId(),
                                20, 50,
                                Optional.empty(), Optional.empty()),
                        new PropertyFormulaRequirements.Part(Powers.MIND_POWER.getId(),
                                10, 100,
                                Optional.of(400), Optional.of(500))
                )), false)

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
                                10,
                                2000,
                                15
                )), false)
                .add(MaterialProperties.FLAMMABILITY, List.of(
                        new IntegerZeroToMaxFormulaOutcome(
                                Powers.VERDANT_POWER.getId(),
                                120,
                                1000,
                                300
                )), false)
                .add(MaterialProperties.ENCHANT_POWER, List.of(
                        new FloatZeroToMaxFormulaOutcome(
                                Powers.MIND_POWER.getId(),
                                50,
                                2000,
                                4.0F
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
                                0.2F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.LIGHT_POWER.getId(),
                                2500,
                                3200,
                                5.0F)
                    ), false)
                .add(MaterialProperties.FRICTION, List.of(
                        new FloatOneToValueFormulaOutcome(
                                Powers.WARP_POWER.getId(),
                                50,
                                1000,
                                0.9F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.FLOW_POWER.getId(),
                                10,
                                1600,
                                0.8F)
                ), false)
                .add(MaterialProperties.BREAK_STRENGTH, List.of(
                        new FloatOneToValueFormulaOutcome(
                                Powers.VITAL_POWER.getId(),
                                100,
                                110,
                                1.5F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.CURSE_POWER.getId(),
                                0,
                                20,
                                0.01F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.BODY_POWER.getId(),
                                0,
                                3200,
                                6.0F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.VERDANT_POWER.getId(),
                                0,
                                3200,
                                2.5F),
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
                                1.05F),
                        new FloatOneToValueFormulaOutcome(
                                Powers.LIGHT_POWER.getId(),
                                2500,
                                3200,
                                5.0F)
                ), false)
                .add(MaterialProperties.MODEL_NAME, List.of(
                        new RangeStringFormulaOutcome(
                                "cracked",
                                Powers.CURSE_POWER.getId(),
                                10,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "smooth",
                                Powers.FLOW_POWER.getId(),
                                10,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "streaked",
                                Powers.X_POWER.getId(),
                                20,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "circles",
                                Powers.Y_POWER.getId(),
                                20,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "smooth",
                                Powers.Z_POWER.getId(),
                                20,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "cracked",
                                Powers.BODY_POWER.getId(),
                                500,
                                Optional.of(1200)
                        ),
                        new RangeStringFormulaOutcome(
                                "streaked",
                                Powers.BODY_POWER.getId(),
                                1200,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "streaked",
                                Powers.VITAL_POWER.getId(),
                                1000,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "smooth",
                                Powers.WARP_POWER.getId(),
                                500,
                                Optional.of(1400)
                        ),
                        new RangeStringFormulaOutcome(
                                "squares",
                                Powers.WARP_POWER.getId(),
                                1400,
                                Optional.of(1500)
                        ),
                        new RangeStringFormulaOutcome(
                                "static",
                                Powers.WARP_POWER.getId(),
                                1500,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "circles",
                                Powers.MIND_POWER.getId(),
                                1500,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "streaked",
                                Powers.VERDANT_POWER.getId(),
                                1000,
                                Optional.empty()
                        ),
                        new RangeStringFormulaOutcome(
                                "cracked",
                                Powers.BLAZE_POWER.getId(),
                                20,
                                Optional.of(1250)
                        ),
                        new RangeStringFormulaOutcome(
                                "smooth",
                                Powers.BLAZE_POWER.getId(),
                                1250,
                                Optional.empty()
                        ),
                        new DefaultStringFormulaOutcome(
                                "default"
                        )
                ), false)
                .add(MaterialProperties.SELF_DEFENSE, List.of(
                        new FloatOneToValueFormulaOutcome(
                                Powers.VITAL_POWER.getId(),
                                1000,
                                1300,
                                3.0F)
                ), false)
        ;

        //noinspection deprecation
        builder(ReactiveDataMaps.MATERIAL_SALT_YIELDS)
                .add(ReactiveItems.SALT_BLOCK, new YieldEntry(16, "salt", 1.0F), false)
                .add(ReactiveItems.ADEPT_SALT_BLOCK, new YieldEntry(32, "squares", 1.33F), false)
                .add(ReactiveItems.CREATION_SALT_BLOCK, new YieldEntry(64, "static", 2.0F), false)
                .add(Items.WHITE_WOOL.builtInRegistryHolder(), new YieldEntry(64, "wool", 0.0F), false)
        ;
    }
}
