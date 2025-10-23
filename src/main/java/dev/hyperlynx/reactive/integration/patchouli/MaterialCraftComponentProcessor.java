package dev.hyperlynx.reactive.integration.patchouli;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import dev.hyperlynx.reactive.alchemy.material.YieldEntry;
import dev.hyperlynx.reactive.alchemy.material.formula.MaterialFormulaMaps;
import dev.hyperlynx.reactive.items.MaterialItem;
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
        base = variables.get("base").as(ItemStack.class);

        String output_material_id = variables.get("output_material").asString();
        ResourceLocation output_material_rl = ResourceLocation.parse(output_material_id);
        if(MaterialMan.occupied(level, output_material_rl)) {
            result = Registration.MATERIAL_ITEM.get().getDefaultInstance();
            MaterialItem.setMaterialId(result, output_material_rl);
            YieldEntry yield = MaterialFormulaMaps.BASE_YIELDS.get(base.getItem().builtInRegistryHolder().key().location());
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
                return IVariable.from(base);
            }
            case "product" -> {
                return IVariable.from(result);
            }
            case "info" -> {
                if (base == ItemStack.EMPTY) {
                    return IVariable.wrap(Component.translatable("docs.reactive.invalid_base_material").getString());
                }
                if (result == ItemStack.EMPTY) {
                    return IVariable.wrap(Component.translatable("docs.reactive.invalid_material").getString());
                }
            }
        }
        return null;
    }
}
