package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;

@Mod.EventBusSubscriber
public class FormulaOutcomeTypes {
    public static final ResourceKey<Registry<MapCodec<? extends FormulaOutcome>>> TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ReactiveMod.location( "formula_outcome_types"));
    public static final Registry<MapCodec<? extends FormulaOutcome>> TYPE_REGISTRY = new RegistryBuilder<>(TYPE_REGISTRY_KEY)
            .sync(true)
            .defaultKey(ReactiveMod.location( "nothing"))
            .create();
    public static final DeferredRegister<MapCodec<? extends FormulaOutcome>> TYPES = DeferredRegister.create(TYPE_REGISTRY, ReactiveMod.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(TYPE_REGISTRY);
    }

    public static final DeferredHolder<MapCodec<? extends FormulaOutcome>, MapCodec<? extends FormulaOutcome>> ZERO_TO_MAX_INT = TYPES
            .register("zero_to_max_integer", () -> IntegerZeroToMaxFormulaOutcome.CODEC.fieldOf("value"));

    public static final DeferredHolder<MapCodec<? extends FormulaOutcome>, MapCodec<? extends FormulaOutcome>> ZERO_TO_MAX_FLOAT = TYPES
            .register("zero_to_max_float", () -> FloatZeroToMaxFormulaOutcome.CODEC.fieldOf("value"));

    public static final DeferredHolder<MapCodec<? extends FormulaOutcome>, MapCodec<? extends FormulaOutcome>> ONE_TO_VALUE_FLOAT = TYPES
            .register("one_to_value_float", () -> FloatOneToValueFormulaOutcome.CODEC.fieldOf("value"));

    public static final DeferredHolder<MapCodec<? extends FormulaOutcome>, MapCodec<? extends FormulaOutcome>> STRING_OPTIONS = TYPES
            .register("string_options", () -> RangeStringFormulaOutcome.CODEC.fieldOf("value"));

    public static final DeferredHolder<MapCodec<? extends FormulaOutcome>, MapCodec<? extends FormulaOutcome>> DEFAULT_STRING = TYPES
            .register("default_string", () -> DefaultStringFormulaOutcome.CODEC.fieldOf("value"));
}
