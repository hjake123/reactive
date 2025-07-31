package dev.hyperlynx.reactive.integration.patchouli;

import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.YieldEntry;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.registration.ReactiveDataMaps;
import dev.hyperlynx.reactive.registration.ReactiveItems;
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
        base = variables.get("base", level.registryAccess()).as(ItemStack.class);

        String output_material_id = variables.get("output_material", level.registryAccess()).asString();
        ResourceLocation output_material_rl = ResourceLocation.parse(output_material_id);
        if(MaterialMan.occupied(level, output_material_rl)) {
            result = ReactiveItems.MATERIAL.get().getDefaultInstance();
            result.set(ReactiveComponentTypes.MATERIAL_ID.get(), output_material_rl);
            YieldEntry yield = base.getItemHolder().getData(ReactiveDataMaps.MATERIAL_SALT_YIELDS);
            if(yield != null) {
                result.setCount(base.getCount() * yield.yield_per_input());
            }
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
