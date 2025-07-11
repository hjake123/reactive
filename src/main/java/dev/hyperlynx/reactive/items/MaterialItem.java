package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.*;
import dev.hyperlynx.reactive.registration.ReactiveComponentTypes;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

    private static final int MAX_NOTES_TOOLTIP_LINE_LENGTH = 32;

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);
        if(!stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            components.add(Component.translatable("text.reactive.random_material_tooltip"));
            return;
        }
        Material material = MaterialMan.fetch(context.level(), stack.get(ReactiveComponentTypes.MATERIAL_ID.get()));
        if(material.getNotes().isPresent()) {
            String first_notes_line = material.getNotes().get().lines().findFirst().orElse("");
            if(!first_notes_line.isEmpty()) {
                if(first_notes_line.length() > MAX_NOTES_TOOLTIP_LINE_LENGTH) {
                    first_notes_line = first_notes_line.substring(0, MAX_NOTES_TOOLTIP_LINE_LENGTH);
                    first_notes_line += "...";
                }
                components.add(Component.literal(first_notes_line).withStyle(ChatFormatting.GRAY));
            }
        }
        if(flag.hasShiftDown()) {
            Component discoverer = material.getDiscovererName(context.level());
            if(!discoverer.equals(Component.empty())) {
                components.add(discoverer);
            }
        }
        if(flag.isAdvanced()) {
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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(!stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            ResourceLocation random_material_id = MaterialMan.createOrFetchByFormula(level, Power.generateRandomPowerCombo(level));
            stack.set(ReactiveComponentTypes.MATERIAL_ID.get(), random_material_id);
        }
        ResourceLocation id = stack.get(ReactiveComponentTypes.MATERIAL_ID.get());
        if(!MaterialMan.occupied(level, id)) {
            stack.remove(ReactiveComponentTypes.MATERIAL_ID.get());
        }
    }
}
