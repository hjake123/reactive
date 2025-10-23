package dev.hyperlynx.reactive.alchemy.material.formula;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.YieldEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MaterialFormulaMaps {
    public static final Map<ResourceLocation, PropertyFormulaRequirements> PROPERTY_FORMULA_MAP = new HashMap<>();
    public static final Map<ResourceLocation, List<FormulaOutcome>> FORMULA_OUTCOME_MAP = new HashMap<>();
    public static final Map<ResourceLocation, YieldEntry> BASE_YIELDS = new HashMap<>();

    public static void init() {
        new MapBuilder<>(PROPERTY_FORMULA_MAP)
                .add(MaterialProperties.MAGMA_STEP, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.BLAZE_POWER.getId(),
                                500, 1000)
                )), false)
                .add(MaterialProperties.FIRE_SOURCE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.BLAZE_POWER.getId(),
                                1, 200)
                )), false)
                .add(MaterialProperties.REDSTONE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.MIND_POWER.getId(),
                                10, 25)
                )), false)
                .add(MaterialProperties.LIGHT, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.LIGHT_POWER.getId(),
                                200, 200)
                )), false)
                .add(MaterialProperties.FLAMMABILITY, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.VERDANT_POWER.getId(),
                                120, 240)
                )), false)
                .add(MaterialProperties.ENCHANT_POWER, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.SOUL_POWER.getId(),
                                50, 250),
                        new PropertyFormulaRequirements.Part(Powers.MIND_POWER.getId(),
                                50, 50)
                )), false)
                .add(MaterialProperties.BLAST_RESISTANCE, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.FRICTION, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.BREAK_STRENGTH, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.MODEL_NAME, new PropertyFormulaRequirements(List.of()), false)
                .add(MaterialProperties.SELF_DEFENSE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.VITAL_POWER.getId(),
                                1000, 1150)
                )), false)
                .add(MaterialProperties.WARPING, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.WARP_POWER.getId(),
                                2000, 2000)
                )), false)
                .add(MaterialProperties.REDSTONE_MELTING, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.ACID_POWER.getId(),
                                20, 50),
                        new PropertyFormulaRequirements.Part(Powers.MIND_POWER.getId(),
                                10, 100)
                )), false)
                .add(MaterialProperties.INTANGIBLE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.SOUL_POWER.getId(),
                                1700, 2300)
                )), false)
                .add(MaterialProperties.SEMITANGIBLE, new PropertyFormulaRequirements(List.of(
                        new PropertyFormulaRequirements.Part(Powers.SOUL_POWER.getId(),
                                800, 1300),
                        new PropertyFormulaRequirements.Part(Powers.WARP_POWER.getId(),
                                10, 20)
                )), false)

        ;


        new MapBuilder<>(FORMULA_OUTCOME_MAP)
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
                                3000,
                                5.0F
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
                                2000,
                                0.9F)
//                        new FloatOneToValueFormulaOutcome(
//                                Powers.FLOW_POWER.getId(),
//                                10,
//                                3200,
//                                0.75F)
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
//                        new RangeStringFormulaOutcome(
//                                "smooth",
//                                Powers.FLOW_POWER.getId(),
//                                10,
//                                Optional.empty()
//                        ),
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
                                2000,
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
        new MapBuilder<>(BASE_YIELDS)
                .add(Registration.SALT_BLOCK_ITEM, new YieldEntry(64, 1, "salt", 1.0F), false)
//                .add(Registration.ADEPT_SALT_BLOCK_ITEM, new YieldEntry(32, 4, "squares", 1.33F), false)
//                .add(Registration.CREATION_SALT_BLOCK_ITEM, new YieldEntry(16, 16, "static", 2.0F), false)
                .add(Items.WHITE_WOOL.builtInRegistryHolder().key().location(), new YieldEntry(64, 1, "wool", 0.0F), false)
        ;
    }

    private static class MapBuilder<T> {
        private final Map<ResourceLocation, T> map;

        MapBuilder(Map<ResourceLocation, T> map) {
            this.map = map;
        }

        MapBuilder<T> add(ResourceLocation id, T item, boolean overwrite) {
            if(overwrite) {
                map.put(id, item);
            } else {
                map.putIfAbsent(id, item);
            }
            return this;
        }

        MapBuilder<T> add(RegistryObject<?> object, T item, boolean overwrite) {
            return add(object.getId(), item, overwrite);
        }
    }
}