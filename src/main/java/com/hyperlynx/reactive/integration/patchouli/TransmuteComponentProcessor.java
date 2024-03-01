package com.hyperlynx.reactive.integration.patchouli;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.recipes.TransmuteRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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
        if(Minecraft.getInstance().level == null)
            return;
        List<TransmuteRecipe> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Registration.TRANS_RECIPE_TYPE.get());
        for(TransmuteRecipe r : recipes){
            if (r.getId().equals(new ResourceLocation(recipeId))) {
                recipe = r;
                break;
            }
        }
    }

    @Override
    public IVariable process(@NotNull String key) {
        if(recipe == null)
            return null;
        if(key.equals("reactant")){
            return IVariable.from(recipe.getReactant().getItems());
        }
        if(key.equals("product")){
            return IVariable.from(recipe.getResultItem());
        }
        if(key.equals("reagents")){
            List<String> reagent_list = new ArrayList<String>();
            for(Power reagent : recipe.getReagents()){
                reagent_list.add(reagent.getName());
            }

            return IVariable.wrap(Component.translatable("docs.reactive.reagent_label").getString() + reagent_list.toString().substring(1, reagent_list.toString().length()-1));
        }
        return null;
    }
}
