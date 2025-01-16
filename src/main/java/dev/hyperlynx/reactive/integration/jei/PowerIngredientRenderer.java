package dev.hyperlynx.reactive.integration.jei;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.util.Color;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PowerIngredientRenderer implements IIngredientRenderer<Power>  {
    @Override
    public void render(GuiGraphics gui, Power ingredient) {
        TextureAtlasSprite sprite = getSprite(ingredient);
        Color color = ingredient.getColor();
        if(ingredient == Powers.ASTRAL_POWER.get()){
            gui.fill(RenderType.END_GATEWAY, 0, 0, 16, 16, 0);
        } else if(!ingredient.invisible) {
            gui.fill(0, 0, 16, 16, 0xEE000000 | color.hex);
        }
        gui.blit(0, 0, 0, 16, 16, sprite,
                (float) color.red / 255, (float) color.green / 255, (float) color.blue / 255, ingredient.invisible ? 0.2F : 1.0F);
    }

    @SuppressWarnings("removal") // Needed for override.
    public List<Component> getTooltip(Power ingredient, TooltipFlag tooltipFlag) {
        List<Component> ret = new ArrayList<>();
        ret.add(Component.literal(ingredient.getName()).append(Component.translatable("text.reactive.power")));
        if(tooltipFlag.isAdvanced()){
            ret.add(Component.literal(ingredient.getResourceLocation().toString()).withStyle(ChatFormatting.GRAY));
        }
        return ret;
    }

    @Override
    public void getTooltip(ITooltipBuilder builder, @NotNull Power ingredient, @NotNull TooltipFlag tooltipFlag) {
        List<Component> tooltip = this.getTooltip(ingredient, tooltipFlag);
        builder.addAll(tooltip);
    }

    private static TextureAtlasSprite getSprite(Power power) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        return dispatcher.getBlockModel(power.getWaterRenderBlock().defaultBlockState()).getParticleIcon(ModelData.EMPTY);
    }
}
