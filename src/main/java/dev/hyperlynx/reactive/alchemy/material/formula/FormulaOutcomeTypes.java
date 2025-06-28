package dev.hyperlynx.reactive.alchemy.material.formula;

import com.mojang.serialization.MapCodec;
import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
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
}
