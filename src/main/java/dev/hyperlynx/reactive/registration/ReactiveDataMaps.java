package dev.hyperlynx.reactive.registration;

import com.mojang.serialization.Codec;
import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperty;
import dev.hyperlynx.reactive.alchemy.material.YieldEntry;
import dev.hyperlynx.reactive.alchemy.material.formula.FormulaOutcome;
import dev.hyperlynx.reactive.alchemy.material.formula.PropertyFormulaRequirements;
import dev.latvian.mods.rhino.ast.Yield;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.List;

@EventBusSubscriber
public class ReactiveDataMaps {
    // TODO Advanced data map?
    public static final DataMapType<MaterialProperty<?>, PropertyFormulaRequirements> PROPERTY_FORMULA_MAP = DataMapType.builder(
            ReactiveMod.location("formula_requirements"),
            MaterialProperties.MATERIAL_PROPERTY_REGISTRY_KEY,
            PropertyFormulaRequirements.CODEC
    ).build();

    public static final DataMapType<MaterialProperty<?>, List<FormulaOutcome>> FORMULA_OUTCOME_MAP = DataMapType.builder(
            ReactiveMod.location("formula_outcomes"),
            MaterialProperties.MATERIAL_PROPERTY_REGISTRY_KEY,
            FormulaOutcome.CODEC.listOf()
    ).build();

    public static final DataMapType<Item, YieldEntry> MATERIAL_SALT_YIELDS = DataMapType.builder(
            ReactiveMod.location("material_bases"),
            Registries.ITEM,
            YieldEntry.CODEC
    ).build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(PROPERTY_FORMULA_MAP);
        event.register(FORMULA_OUTCOME_MAP);
        event.register(MATERIAL_SALT_YIELDS);
    }
}
