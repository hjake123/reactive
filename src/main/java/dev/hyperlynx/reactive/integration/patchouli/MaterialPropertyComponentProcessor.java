package dev.hyperlynx.reactive.integration.patchouli;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperties;
import dev.hyperlynx.reactive.alchemy.material.MaterialProperty;
import dev.hyperlynx.reactive.alchemy.material.formula.PropertyFormulaRequirements;
import dev.hyperlynx.reactive.net.ReactionPageFetcher;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class MaterialPropertyComponentProcessor implements IComponentProcessor {
    ResourceLocation property_id = ReactiveMod.location("error");
    @Override
    public void setup(Level level, IVariableProvider variables) {
        property_id = ResourceLocation.parse(variables.get("property", level.registryAccess()).asString());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable IVariable process(Level level, String key) {
        if(key.equals("formula")){
            var property = property();
            List<PropertyFormulaRequirements.Part> requirements = Objects.requireNonNull(property.getData(ReactiveDataMaps.PROPERTY_FORMULA_MAP)).requirements();
            StringBuilder formula = new StringBuilder();
            formula.append(Component.translatable("docs.reactive.formula_label").getString());
            requirements.forEach(part -> {
                formula.append(Component.translatable("docs.reactive.at_least").getString());
                formula.append(part.lowBound(property_id) / 16);
                formula.append("% ");
                formula.append(Component.translatable(part.power_id().toLanguageKey("power")).getString());
                formula.append("$(br)");
            });

            return IVariable.wrap(formula.toString(), level.registryAccess());
        }
        if(key.equals("outcome")){
            var property = property();
            var outcome = Objects.requireNonNull(property.getData(ReactiveDataMaps.FORMULA_OUTCOME_MAP));

            return IVariable.wrap("TODO", level.registryAccess());
        }
        return null;
    }

    private Holder.Reference<MaterialProperty<?>> property() {
        Optional<Holder.Reference<MaterialProperty<?>>> property = MaterialProperties.PROPERTY_REGISTRY.getHolder(property_id);
        if(property.isEmpty()) {
            throw new NoSuchElementException("No material property with id " + property_id);
        }
        return property.get();
    }
}
