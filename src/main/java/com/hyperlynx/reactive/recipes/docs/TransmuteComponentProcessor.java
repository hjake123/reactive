package com.hyperlynx.reactive.recipes.docs;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.recipes.TransmuteRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;

public class TransmuteComponentProcessor implements IComponentProcessor {

    private TransmuteRecipe recipe;

    @Override
    public void setup(IVariableProvider variables) {
        String recipeId = "reactive:transmutation/" + variables.get("recipe").asString();
        List<TransmuteRecipe> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Registration.TRANS_RECIPE_TYPE.get());
        for(TransmuteRecipe r : recipes){
            if (r.getId().equals(new ResourceLocation(recipeId))) {
                recipe = r;
                break;
            }
        }
        if(recipe == null)
            throw new IllegalArgumentException();
    }

    @Override
    public IVariable process(@NotNull String key) {
        if(key.equals("reactant")){
            return IVariable.from(recipe.getReactant());
        }
        if(key.equals("product")){
            return IVariable.from(recipe.getResultItem());
        }
        if(key.equals("reagents")){
            List<String> reagent_list = new ArrayList<String>();
            for(Power reagent : recipe.getReagents()){
                reagent_list.add(reagent.getName());
            }

            return IVariable.wrap("$(4)Reagents: " + reagent_list.toString().substring(1, reagent_list.toString().length()-1));
        }
        return null;
    }
}