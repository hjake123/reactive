package dev.hyperlynx.reactive.integration.patchouli;

import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class MaterialCraftComponentProcessor implements IComponentProcessor {
    private ItemStack base = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        String base_id = variables.get("base", level.registryAccess()).asString();
        ResourceLocation base_rl = ResourceLocation.parse(base_id);
        base = BuiltInRegistries.ITEM.get(base_rl).getDefaultInstance();

        String output_material_id = variables.get("output_material", level.registryAccess()).asString();
        ResourceLocation output_material_rl = ResourceLocation.parse(output_material_id);
        if(MaterialMan.occupied(level, output_material_rl)) {
            result = ReactiveItems.MATERIAL.get().getDefaultInstance();
            result.set(ReactiveComponentTypes.MATERIAL_ID.get(), output_material_rl);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public @Nullable IVariable process(Level level, @NotNull String key) {
        switch (key) {
            case "reactant" -> {
                return IVariable.from(base, level.registryAccess());
            }
            case "product" -> {
                return IVariable.from(result, level.registryAccess());
            }
            case "info" -> {
                if (base == ItemStack.EMPTY) {
                    return IVariable.wrap(Component.translatable("docs.reactive.invalid_base_material").getString(), level.registryAccess());
                }
                if (result == ItemStack.EMPTY) {
                    return IVariable.wrap(Component.translatable("docs.reactive.invalid_material").getString(), level.registryAccess());
                }
            }
        }
        return null;
    }
}
