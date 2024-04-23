package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.ConfigMan;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LitmusPaperItem extends Item {
    public static final String TAG_MEASUREMENT = "Measurement";
    public static final String TAG_STATUS = "Status";
    public LitmusPaperItem(Properties props) {
        super(props.stacksTo(1));
    }

    // Create a list of lines that is the measurment.
    private List<Component> buildMeasurmentText(ItemStack stack, int water_color){
        List<Component> text = new ArrayList<>();
        if(!stack.hasTag())
            return text;

        ListTag measurements = stack.getTag().getList(TAG_MEASUREMENT, Tag.TAG_COMPOUND);
        for(Tag tag : measurements){
            if(tag instanceof CompoundTag measurement){
                String m = "";
                TextColor color = TextColor.fromRgb(0xFFFFFF);
                if(measurement.contains("power") && ConfigMan.CLIENT.colorizeLitmusOutput.get()){
                    color = Power.readPower(measurement, "power").getTextColor();
                }
                m += measurement.getString("value");
                text.add(Component.literal(m).withStyle(Style.EMPTY.withColor(color)));
            }
        }

        if(measurements.isEmpty()){
            text.add(Component.translatable("text.reactive.measurement_empty")
                    .withStyle(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? Style.EMPTY.withColor(water_color) : Style.EMPTY));
        }

        StringTag reaction_status = (StringTag) stack.getTag().get(TAG_STATUS);
        if(reaction_status == null)
            return text;

        switch(Reaction.Status.valueOf(reaction_status.getAsString())){
            case STABLE -> text.add(Component.translatable("text.reactive.stable").withStyle(ChatFormatting.GRAY));
            case VOLATILE -> text.add(Component.translatable("text.reactive.single_power_reaction_missing_condition").withStyle(ChatFormatting.GRAY));
            case POWER_TOO_WEAK -> text.add(Component.translatable("text.reactive.power_too_weak").withStyle(ChatFormatting.GRAY));
            case MISSING_STIMULUS -> text.add(Component.translatable("text.reactive.multi_power_reaction_missing_condition").withStyle(ChatFormatting.GRAY));
            case MISSING_CATALYST -> text.add(Component.translatable("text.reactive.missing_catalyst").withStyle(ChatFormatting.GRAY));
            case INHIBITED -> text.add(Component.translatable("text.reactive.inhibited").withStyle(ChatFormatting.GRAY));
            case REACTING -> text.add(Component.translatable("text.reactive.reacting"));
        }
        return text;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> hover_text, TooltipFlag tooltip_flag) {
        super.appendHoverText(stack, level, hover_text, tooltip_flag);
        if(stack.hasTag()) {
            hover_text.add(Component.translatable("text.reactive.litmus_instructions"));
        }
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide){
            if(!player.getItemInHand(hand).hasTag())
                return InteractionResultHolder.pass(player.getItemInHand(hand));

            player.sendSystemMessage(Component.translatable("text.reactive.measurement_header").withStyle(ChatFormatting.GRAY));
            for(Component line : buildMeasurmentText(player.getItemInHand(hand), BiomeColors.getAverageWaterColor(level, player.getOnPos()))){
                player.sendSystemMessage(line);
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!(context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof CrucibleBlock)){
            return InteractionResult.PASS;
        }

        CrucibleBlockEntity crucible = (CrucibleBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
        if(crucible == null) {
            return InteractionResult.PASS;
        }

        takeMeasurement(context.getItemInHand(), crucible);

        return InteractionResult.SUCCESS;
    }

    public static void takeMeasurement(ItemStack paper, CrucibleBlockEntity crucible) {
        ListTag measurements = new ListTag();

        if(crucible.integrity < 85){
            CompoundTag warning = new CompoundTag();
            warning.putString("value",  Component.translatable("text.reactive.litmus_integrity_failure").getString());
            measurements.add(warning);
        }

        for(Power p : crucible.getPowerMap().keySet()){
            int pow = crucible.getPowerLevel(p);
            if(pow == 0)
                continue;

            String measurement = p.getName().toUpperCase() + " - " + getPercent(pow);

            CompoundTag mt = new CompoundTag();
            mt.putString("power", p.getId());
            mt.putString("value", measurement);
            measurements.add(mt);
        }

        if(!paper.hasTag())
            paper.setTag(new CompoundTag());
        paper.getTag().put(TAG_MEASUREMENT, measurements);
        paper.getTag().put(TAG_STATUS, StringTag.valueOf(crucible.reaction_status.toString()));

    }

    @NotNull
    public static String getPercent(int pow) {
        return pow > 16 ? pow / 16 + "%" : Component.translatable("text.reactive.trace").getString();
    }
}
