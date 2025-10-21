package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.*;
import dev.hyperlynx.reactive.alchemy.material.formula.Formula;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MaterialItem extends BlockItem {
    public static final String MATERIAL_ID_KEY = "MaterialId";

    public MaterialItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean hasMaterialId(ItemStack stack) {
        return stack.hasTag() && stack.getOrCreateTag().contains(MATERIAL_ID_KEY);
    }

    public static void setMaterialId(ItemStack stack, ResourceLocation id) {
        stack.getOrCreateTag().putString(MATERIAL_ID_KEY, id.toString());
    }

    public static void removeMaterialId(ItemStack stack) {
        stack.removeTagKey(MATERIAL_ID_KEY);
    }

    public static @Nullable ResourceLocation getMaterialId(ItemStack stack) {
        if(hasMaterialId(stack)) {
            return ResourceLocation.parse(stack.getOrCreateTag().getString(MATERIAL_ID_KEY));
        }
        return null;
    }

    @Override
    public Component getName(ItemStack stack) {
        if(hasMaterialId(stack)) {
            return ClientMaterialMan.getName(Objects.requireNonNull(getMaterialId(stack)));
        }
        return Component.translatable("block.reactive.invalid_material");
    }

    private static final int MAX_NOTES_TOOLTIP_LINE_LENGTH = 32;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        if(!hasMaterialId(stack)) {
            components.add(Component.translatable("text.reactive.random_material_tooltip"));
            return;
        }
        Material material = MaterialMan.fetch(level, getMaterialId(stack));
        if(!material.getNotes().isEmpty()) {
            String first_notes_line = material.getNotes().lines().findFirst().orElse("");
            if(!first_notes_line.isEmpty()) {
                if(first_notes_line.length() > MAX_NOTES_TOOLTIP_LINE_LENGTH) {
                    first_notes_line = first_notes_line.substring(0, MAX_NOTES_TOOLTIP_LINE_LENGTH);
                    first_notes_line += "...";
                }
                components.add(Component.literal(first_notes_line).withStyle(ChatFormatting.GRAY));
            }
        }

        Component discoverer = material.getDiscovererName(level);
        if(!discoverer.equals(Component.empty())) {
            components.add(discoverer);
        }

        if(flag.isAdvanced()) {
            components.add(Component.literal("" + getMaterialId(stack)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static int getItemColor(ItemStack stack, int ignored) {
        if(stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            return ClientMaterialMan.data().get(stack.get(ReactiveComponentTypes.MATERIAL_ID.get())).getOrDefault(MaterialProperties.COLOR.get(), Color.white()).hex();
        }
        return 0;
    }

    public static float getModelOverrideValue(ItemStack stack, Level level, LivingEntity ignored, long ignoredSeed) {
        if(!stack.has(ReactiveComponentTypes.MATERIAL_ID.get())) {
            return 0.0F;
        }
        Material material = MaterialMan.fetch(level, stack.get(ReactiveComponentTypes.MATERIAL_ID.get()));
        return MaterialModel.fromName(material.getOrDefault(MaterialProperties.MODEL_NAME.get(), "SALT")).getModelIndex();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(level.isClientSide()){
            return;
        }
        if(!hasMaterialId(stack)) {
            ResourceLocation random_material_id = MaterialMan.createOrFetchByFormula(level, new Formula(Power.generateRandomPowerCombo(level), ReactiveItems.SALT_BLOCK));
            setMaterialId(stack, random_material_id);
        }
        ResourceLocation id = getMaterialId(stack);
        if(!MaterialMan.occupied(level, id)) {
            removeMaterialId(stack);
        }
    }
}
