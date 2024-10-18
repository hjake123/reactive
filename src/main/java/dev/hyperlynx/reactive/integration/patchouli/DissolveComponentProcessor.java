package dev.hyperlynx.reactive.integration.patchouli;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.recipes.DissolveRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class DissolveComponentProcessor implements IComponentProcessor {

    private DissolveRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        String reactant = variables.get("reactant", level.registryAccess()).asString();
        List<RecipeHolder<DissolveRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(Registration.DISSOLVE_RECIPE_TYPE.get());
        for(RecipeHolder<DissolveRecipe> r : recipes){
            for(ItemStack i : r.value().getReactant().getItems()){
                if (i.getItem().equals(BuiltInRegistries.ITEM.get(ResourceLocation.parse(reactant))))
                    recipe = r.value();
            }
        }
    }

    @Override
    public IVariable process(Level level, @NotNull String key) {
        if(recipe == null) {
            return IVariable.empty();
        }
        if(key.equals("product")){
            return IVariable.from(recipe.getResultItem(level.registryAccess()), level.registryAccess());
        }
        return null;
    }
}
