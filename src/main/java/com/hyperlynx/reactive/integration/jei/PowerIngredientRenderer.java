//package com.hyperlynx.reactive.integration.jei;
//
//import com.hyperlynx.reactive.alchemy.Power;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.ingredients.IIngredientRenderer;
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.TooltipFlag;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.hyperlynx.reactive.integration.jei.ReactiveJEIPlugin.HELPERS;
//
//public class PowerIngredientRenderer implements IIngredientRenderer<Power>  {
//    @Override
//    public void render(GuiGraphics gui, Power ingredient) {
//        if(ingredient.getRenderStack() != null){
//            ReactiveJEIPlugin.HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, ingredient.getRenderStack()).draw(gui);
//        }
//    }
//
//    @Override
//    public List<Component> getTooltip(Power ingredient, TooltipFlag tooltipFlag) {
//        List<Component> ret = new ArrayList<>();
//        ret.add(Component.literal(ingredient.getName() + " Power"));
//        if(tooltipFlag.isAdvanced()){
//            ret.add(Component.literal("reactive:" + ingredient.getId()).withStyle(ChatFormatting.GRAY));
//        }
//        return ret;
//    }
//}
