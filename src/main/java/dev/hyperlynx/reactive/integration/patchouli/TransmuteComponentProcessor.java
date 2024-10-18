package dev.hyperlynx.reactive.integration.patchouli;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.recipes.TransmuteRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;

public class TransmuteComponentProcessor implements IComponentProcessor {

    private TransmuteRecipe recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        String recipe_id = "reactive:transmutation/" + variables.get("recipe", level.registryAccess()).asString();
        RecipeManager manager = level.getRecipeManager();
        recipe = (TransmuteRecipe) manager.byKey(ResourceLocation.parse(recipe_id)).orElseThrow().value();
    }

    @Override
    public IVariable process(Level level, @NotNull String key) {
        if(recipe != null && key.equals("reactant")){
            return IVariable.from(recipe.getReactant().getItems(), level.registryAccess());
        }
        if(recipe != null && key.equals("product")){
            return IVariable.from(recipe.getResultItem(level.registryAccess()), level.registryAccess());
        }
        if(key.equals("reagents")){
            if(recipe == null){
                return IVariable.wrap(Component.translatable("docs.reactive.removed_recipe").getString(), level.registryAccess());
            }
            List<String> reagent_list = new ArrayList<>();
            for(Power reagent : recipe.getReagents()){
                reagent_list.add(reagent.getName());
            }

            return IVariable.wrap(Component.translatable("docs.reactive.reagent_label").getString() + reagent_list.toString().substring(1, reagent_list.toString().length()-1), level.registryAccess());
        }
        return null;
    }
}
