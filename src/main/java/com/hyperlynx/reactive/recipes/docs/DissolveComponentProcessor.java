package com.hyperlynx.reactive.recipes.docs;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.recipes.DissolveRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class DissolveComponentProcessor implements IComponentProcessor {

    private DissolveRecipe recipe;

    @Override
    public void setup(IVariableProvider variables) {
        String reactant = variables.get("reactant").asString();
        List<DissolveRecipe> recipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(Registration.DISSOLVE_RECIPE_TYPE.get());
        for(DissolveRecipe r : recipes){
            for(ItemStack i : r.getReactant().getItems()){
                if (i.getItem().equals(CraftingHelper.getItem(reactant, false)))
                    recipe = r;
            }
        }
        if(recipe == null)
            throw new IllegalArgumentException();
    }

    @Override
    public IVariable process(@NotNull String key) {
        if(key.equals("product")){
            return IVariable.from(recipe.getResultItem());
        }
        return null;
    }
}
