package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.ConfigMan;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DissolveRecipeCategory implements IRecipeCategory<RecipeHolder<DissolveRecipe>> {
    SlotManager slot_manager = new SlotManager();

    public DissolveRecipeCategory() {
        slot_manager.addSlot("reactant", 1, 1);
        slot_manager.addSlot("product", 55, 1);
        slot_manager.addSlot("power_result", 55, 22);
    }
    @Override
    public @Nullable ResourceLocation getRegistryName(@Nullable RecipeHolder<DissolveRecipe> holder) {
        return holder.id();
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<DissolveRecipe>> getRecipeType() {
        return RecipeType.createFromVanilla(Registration.DISSOLVE_RECIPE_TYPE.get());
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("title.reactive.dissolve");
    }

    public IDrawable background() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawable(ReactiveMod.location("textures/gui/tf_jei.png"), 2, 2, 72, 39);
    }

    @Override
    public int getHeight() {
        return background().getHeight();
    }

    @Override
    public int getWidth() {
        return background().getWidth();
    }

    @Override
    public IDrawable getIcon() {
        return ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, Registration.CRUCIBLE_ITEM.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DissolveRecipe> holder, @NotNull IFocusGroup focuses) {
        DissolveRecipe recipe = holder.value();

        IRecipeSlotBuilder input_slot = slot_manager.buildSlot(builder, "reactant", RecipeIngredientRole.INPUT);
        IRecipeSlotBuilder output_slot = slot_manager.buildSlot(builder, "product", RecipeIngredientRole.OUTPUT);
        input_slot.addItemStacks(List.of(recipe.getReactant().getItems()));
        output_slot.addItemStack(recipe.getProduct());

        if(ConfigMan.CLIENT.showPowerSources.get()){
            IRecipeSlotBuilder power_slot = slot_manager.buildSlot(builder, "power_result", RecipeIngredientRole.OUTPUT);
            for (ItemStack input : recipe.getReactant().getItems()) {
                assert Minecraft.getInstance().level != null;
                power_slot.addIngredients(ReactiveJEIPlugin.POWER_TYPE, Power.getSourcePower(Minecraft.getInstance().level.registryAccess(), input));
            }
        }
    }

    @Override
    public void draw(RecipeHolder<DissolveRecipe> holder, @Nullable IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics gui, double mouseX, double mouseY) {
        background().draw(gui);
        if(holder.value().needs_electricity){
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
        int center = getWidth() / 2;
        int x = center - (width / 2);
        int y = 11;
        gui.drawString(minecraft.font, "Charge",  x, y,0x0DA8A8);
    }
}
