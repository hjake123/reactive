//package com.hyperlynx.reactive.recipes.jei;
//
//import com.hyperlynx.reactive.ReactiveMod;
//import com.hyperlynx.reactive.alchemy.Power;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.math.Transformation;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.ingredients.IIngredientRenderer;
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.TooltipFlag;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.hyperlynx.reactive.recipes.jei.ReactiveJEIPlugin.HELPERS;
//
//public class PowerIngredientRenderer implements IIngredientRenderer<Power>  {
//    @Override
//    public void render(PoseStack stack, Power ingredient) {
//        if(ingredient.getRenderStack() != null){
//            HELPERS.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, ingredient.getRenderStack()).draw(stack);
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
