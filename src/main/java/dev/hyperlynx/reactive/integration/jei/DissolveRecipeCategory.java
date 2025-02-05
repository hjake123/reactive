package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.recipes.DissolveRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import dev.hyperlynx.reactive.ConfigMan;

import java.util.List;

import static dev.hyperlynx.reactive.integration.jei.ReactiveJEIPlugin.POWER_TYPE;

public class DissolveRecipeCategory implements IRecipeCategory<DissolveRecipe> {
    SlotManager slot_manager = new SlotManager();

    public DissolveRecipeCategory() {
        slot_manager.addSlot("reactant", 1, 1);
        slot_manager.addSlot("product", 55, 1);
        slot_manager.addSlot("power_result", 55, 22);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(DissolveRecipe recipe) {
        return recipe.getId();
    }

    @Override
    public RecipeType<DissolveRecipe> getRecipeType() {
        return RecipeType.create(ReactiveMod.MODID, "dissolve", DissolveRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.reactive.dissolve");
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(ReactiveMod.location("textures/gui/tf_jei.png"), 2, 2, 72, 39);
    }

    @Override
    public IDrawable getIcon() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DissolveRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder input_slot = slot_manager.buildSlot(builder, "reactant", RecipeIngredientRole.INPUT);
        IRecipeSlotBuilder output_slot = slot_manager.buildSlot(builder, "product", RecipeIngredientRole.OUTPUT);

        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));
        output_slot.addItemStack(recipe.getResultItem());

        if(ConfigMan.CLIENT.showPowerSources.get()){
            IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "power_result", RecipeIngredientRole.OUTPUT);
            for (ItemStack input : recipe.getReactant().getItems()) {
                power_slot.addIngredients(POWER_TYPE, Power.getSourcePower(input));
            }
        }
    }

    @Override
    public void draw(DissolveRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        if(recipe.needs_electricity){
            drawElectricLabel(gui);
        }
        slot_manager.drawSlotBackgrounds(gui, List.of("reactant", "product"));
        if(ConfigMan.CLIENT.showPowerSources.get()) {
            slot_manager.drawSlotBackground(gui, "power_result");
        }
    }

    private void drawElectricLabel(GuiGraphics gui) {
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.font.width("Charge");
        int center = getBackground().getWidth() / 2;
        int x = center - (width / 2);
        int y = 11;
        gui.drawString(minecraft.font, "Charge",  x, y,0x0DA8A8);
    }
}
