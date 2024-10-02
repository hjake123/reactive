package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.ConfigMan;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LitmusPaperItem extends Item {
    public static final String TAG_MEASUREMENT = "Measurement";
    public static final String TAG_STATUS = "Status";
    public static final String TAG_MULTI_STATUS = "MultiStatus";
    public static final String TAG_UNRESOLVED_STATUS = "UnresolvedStatus";
    public LitmusPaperItem(Properties props) {
        super(props.stacksTo(1));
    }

    // Create a list of lines that is the measurement.
    private List<Component> buildMeasurementText(ItemStack stack, int water_color, boolean crouching){
        List<Component> text = new ArrayList<>();
        if(!stack.hasTag())
            return text;

        assert stack.getTag() != null;
        if(stack.getTag().contains(TAG_STATUS) || !crouching) {
            // Show the measurements either every time for a legacy Litmus Paper or when not crouching for a new one.
            ListTag measurements = stack.getTag().getList(TAG_MEASUREMENT, Tag.TAG_COMPOUND);
            for (Tag tag : measurements) {
                if (tag instanceof CompoundTag measurement) {
                    String m = "";
                    TextColor color = TextColor.fromRgb(0xFFFFFF);
                    if (measurement.contains("power") && ConfigMan.CLIENT.colorizeLitmusOutput.get()) {
                        color = Power.readPower(measurement, "power").getTextColor();
                    }
                    m += measurement.getString("value");
                    text.add(Component.literal(m).withStyle(Style.EMPTY.withColor(color)));
                }
            }

            if (measurements.isEmpty()) {
                text.add(Component.translatable("text.reactive.measurement_empty")
                        .withStyle(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? Style.EMPTY.withColor(water_color) : Style.EMPTY));
            }
        }

        if(stack.getTag().contains(TAG_STATUS)){
            // Legacy behavior for old Litmus Paper stacks.
            StringTag reaction_status = (StringTag) stack.getTag().get(TAG_STATUS);
            if(reaction_status == null)
                return text;
            text.add(statusComponent(reaction_status.getAsString()));
        } else if(stack.getTag().contains(TAG_MULTI_STATUS) && crouching) {
            // New behavior.
            ListTag status_list = stack.getTag().getList(TAG_MULTI_STATUS, Tag.TAG_STRING);
            for(Tag status_tag : status_list){
                if(status_tag instanceof StringTag){
                    text.add(Component.literal(status_tag.getAsString()));
                }
            }
        }

        return text;
    }

    private MutableComponent statusComponent(String status){
        switch(Reaction.Status.valueOf(status)){
            case STABLE -> {
                return Component.translatable("text.reactive.stable").withStyle(ChatFormatting.GRAY);
            }
            case VOLATILE -> {
                return Component.translatable("text.reactive.single_power_reaction_missing_condition").withStyle(ChatFormatting.GRAY);
            }
            case POWER_TOO_WEAK -> {
                return Component.translatable("text.reactive.power_too_weak").withStyle(ChatFormatting.GRAY);
            }
            case MISSING_STIMULUS -> {
                return Component.translatable("text.reactive.multi_power_reaction_missing_condition").withStyle(ChatFormatting.GRAY);
            }
            case MISSING_CATALYST -> {
                return Component.translatable("text.reactive.missing_catalyst").withStyle(ChatFormatting.GRAY);
            }
            case INHIBITED -> {
                return Component.translatable("text.reactive.inhibited").withStyle(ChatFormatting.GRAY);
            }
            case REACTING -> {
                return Component.translatable("text.reactive.reacting");
            }
        }
        return Component.translatable("reaction.reactive.unknown").withStyle(ChatFormatting.RED);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> hover_text, TooltipFlag tooltip_flag) {
        super.appendHoverText(stack, level, hover_text, tooltip_flag);
        if(stack.hasTag()) {
            if(stack.getTag().contains(TAG_STATUS)){
                hover_text.add(Component.translatable("text.reactive.legacy_litmus_instructions"));

            } else {
                hover_text.add(Component.translatable("text.reactive.litmus_instructions"));
                hover_text.add(Component.translatable("text.reactive.litmus_instructions_2"));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(level.isClientSide){
            if(!player.getItemInHand(hand).hasTag())
                return InteractionResultHolder.pass(player.getItemInHand(hand));

            player.sendSystemMessage(Component.translatable("text.reactive.measurement_header").withStyle(ChatFormatting.GRAY));
            for(Component line : buildMeasurementText(player.getItemInHand(hand), BiomeColors.getAverageWaterColor(level, player.getOnPos()), player.isCrouching())){
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

        assert paper.getTag() != null;
        if(paper.getTag().contains(TAG_STATUS)){
            // Migrate this Litmus Paper to the new format.
            paper.getTag().remove(TAG_STATUS);
        }
        paper.getTag().put(TAG_MEASUREMENT, measurements);

        ListTag unresolved_status_list = new ListTag();
        for(ReactionStatusEntry status : crucible.reaction_status){
            CompoundTag reaction_data = new CompoundTag();
            reaction_data.put("reaction", StringTag.valueOf(status.reaction_alias()));
            reaction_data.put("status", StringTag.valueOf(status.getStatusAsString()));
            unresolved_status_list.add(reaction_data);
        }
        paper.getTag().put(TAG_UNRESOLVED_STATUS, unresolved_status_list);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity holder, int tick, boolean unknown) {
        // After taking the measurement, this method ticks on the server side to update the item's tags
        // and finalize the reaction status info with player-specific data.
        super.inventoryTick(stack, level, holder, tick, unknown);

        if(holder instanceof ServerPlayer player && stack.hasTag() && stack.getTag().contains(TAG_UNRESOLVED_STATUS)){
            ListTag multi_status = new ListTag();
            for(Tag tag : stack.getTag().getList(TAG_UNRESOLVED_STATUS, Tag.TAG_COMPOUND)){
                if(tag instanceof CompoundTag reaction_data){
                    String alias = reaction_data.get("reaction").getAsString();
                    String status = reaction_data.get("status").getAsString();
                    MutableComponent full_output_line = getReactionOrUnknownComponent(alias, player);
                    full_output_line.append(" ");
                    full_output_line.append(statusComponent(status));
                    multi_status.add(StringTag.valueOf(full_output_line.getString()));
                }
            }
            stack.getTag().put(TAG_MULTI_STATUS, multi_status);
            stack.getTag().remove(TAG_UNRESOLVED_STATUS);
        }
    }

    private MutableComponent getReactionOrUnknownComponent(String reaction_alias, ServerPlayer player){
        if(player.getAdvancements().getOrStartProgress(Advancement.Builder.advancement().build(new ResourceLocation(ReactiveMod.MODID, "reactions/" + reaction_alias))).isDone())
            return Component.translatable("reaction.reactive." + reaction_alias);
        else
            return Component.translatable("reaction.reactive.unknown");
    }

    @NotNull
    public static String getPercent(int pow) {
        return pow > 16 ? pow / 16 + "%" : Component.translatable("text.reactive.trace").getString();
    }
}
