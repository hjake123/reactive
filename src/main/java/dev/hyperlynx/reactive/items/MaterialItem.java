package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.alchemy.material.*;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Objects;

public class MaterialItem extends BlockItem {
    public MaterialItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        if(stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            return ClientMaterialMan.getName(Objects.requireNonNull(stack.get(ReactiveComponentTypes.MATERIAL_ID.get())));
        }
        return Component.translatable("block.reactive.invalid_material");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);
        if(flag.isAdvanced() && stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            components.add(Component.literal("" + stack.get(ReactiveComponentTypes.MATERIAL_ID.get())).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static int getItemColor(ItemStack stack, int index) {
        if(stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            return ClientMaterialMan.data().get(stack.get(ReactiveComponentTypes.MATERIAL_ID.get())).getOrDefault(MaterialProperties.COLOR.get(), Color.white()).hex();
        }
        return 0;
    }

    public static float getModelOverrideValue(ItemStack stack, Level level, LivingEntity holder, long seed) {
        if(!stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            return 0.0F;
        }
        Material material = MaterialMan.fetch(level, stack.get(ReactiveComponentTypes.MATERIAL_ID.get()));
        return MaterialModel.fromName(material.getOrDefault(MaterialProperties.MODEL_NAME.get(), "SALT")).getModelIndex();
    }
}
