package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.Powers;
import com.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import com.hyperlynx.reactive.ConfigMan;
import com.hyperlynx.reactive.components.LitmusMeasurement;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.ArrayList;
import java.util.List;

import static com.hyperlynx.reactive.Registration.ISOLATE_OMEN_TRIGGER;

public class LitmusPaperItem extends Item {
    public LitmusPaperItem(Properties props) {
        super(props.stacksTo(1));
    }

    // Create a list of lines that is the measurement.
    private List<Component> buildMeasurementText(ItemStack stack, Player player){
        List<Component> text = new ArrayList<>();
        LitmusMeasurement measurement = stack.get(Registration.LITMUS_MEASUREMENT.get());
        if(measurement == null){
            return text;
        }

        boolean mode = !player.isCrouching();

        if(measurement.integrity_violated()){
            text.add(Component.translatable("text.reactive.litmus_integrity_failure"));
        }

        if(mode){
            if(player.level().isClientSide){
                // This must be done on the client to allow for querying the water color.
                text.add(Component.translatable("text.reactive.measurement_header").withStyle(ChatFormatting.GRAY));
                for(LitmusMeasurement.Line line : measurement.measurements()){
                    TextColor color = TextColor.fromRgb(0xFFFFFF);
                    if(ConfigMan.CLIENT.colorizeLitmusOutput.get()){
                        Power power = Powers.POWER_REGISTRY.get(line.power());
                        if(power != null) {
                            color = power.getTextColor();
                        }
                        if(power == Powers.OMEN_POWER.get() && player instanceof ServerPlayer splayer){
                            ISOLATE_OMEN_TRIGGER.get().trigger(splayer);
                        }
                    }
                    text.add(Component.literal(line.line()).withStyle(Style.EMPTY.withColor(color)));
                }

                if(measurement.measurements().isEmpty()){
                    text.add(Component.translatable("text.reactive.measurement_empty")
                            .withStyle(ConfigMan.CLIENT.colorizeLitmusOutput.get() ? Style.EMPTY.withColor(BiomeColors.getAverageWaterColor(player.level(), player.getOnPos())) : Style.EMPTY));
                }
            }
        }else{
            if(!player.level().isClientSide){
                // This must be done on the server to allow for querying the player's advancements.
                text.add(Component.translatable("text.reactive.measurement_header").withStyle(ChatFormatting.GRAY));
                for(ReactionStatusEntry entry : measurement.statuses()){
                    switch(entry.status()){
                        case STABLE -> text.add(Component.translatable("text.reactive.stable").withStyle(ChatFormatting.GRAY));
                        case VOLATILE -> text.add(getReactionOrUnknownComponent(entry, player)
                                .append(Component.translatable("text.reactive.single_power_reaction_missing_condition").withStyle(ChatFormatting.GRAY)));
                        case POWER_TOO_WEAK -> text.add(getReactionOrUnknownComponent(entry, player)
                                .append(Component.translatable("text.reactive.power_too_weak").withStyle(ChatFormatting.GRAY)));
                        case MISSING_STIMULUS -> text.add(getReactionOrUnknownComponent(entry, player)
                                .append(Component.translatable("text.reactive.multi_power_reaction_missing_condition").withStyle(ChatFormatting.GRAY)));
                        case MISSING_CATALYST -> text.add(getReactionOrUnknownComponent(entry, player)
                                .append(Component.translatable("text.reactive.missing_catalyst").withStyle(ChatFormatting.GRAY)));
                        case INHIBITED -> text.add(getReactionOrUnknownComponent(entry, player)
                                .append(Component.translatable("text.reactive.inhibited").withStyle(ChatFormatting.GRAY)));
                        case REACTING -> text.add(getReactionOrUnknownComponent(entry, player)
                                .append(Component.translatable("text.reactive.reacting")));
                    }
                }
            }
        }
        return text;
    }

    private MutableComponent getReactionOrUnknownComponent(String reaction_alias, Player player){
        if(player instanceof ServerPlayer splayer){
            if(splayer.getAdvancements().getOrStartProgress(Advancement.Builder.advancement().build(ReactiveMod.location("reactions/"+reaction_alias))).isDone())
                return Component.translatable("reaction.reactive." + reaction_alias);
            else
                return Component.translatable("reaction.reactive.unknown");
        }
        return Component.literal("Error");
    }

    private MutableComponent getReactionOrUnknownComponent(ReactionStatusEntry entry, Player player){
        return getReactionOrUnknownComponent(entry.reaction_alias(), player);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> hover_text, TooltipFlag tooltip_flag) {
        super.appendHoverText(stack, context, hover_text, tooltip_flag);
        if(stack.has(Registration.LITMUS_MEASUREMENT)) {
            hover_text.add(Component.translatable("text.reactive.litmus_instructions"));
            hover_text.add(Component.translatable("text.reactive.litmus_instructions_2"));
        }
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!player.getItemInHand(hand).has(Registration.LITMUS_MEASUREMENT))
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        for(Component line : buildMeasurementText(player.getItemInHand(hand), player)){
            player.sendSystemMessage(line);
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

    public static void takeMeasurement(ItemStack paper, CrucibleBlockEntity crucible){
        List<LitmusMeasurement.Line> lines = new ArrayList<>();

        for(Power power : crucible.getPowerMap().keySet()) {
            int power_level = crucible.getPowerLevel(power);
            if(power_level == 0)
                continue;

            lines.add(new LitmusMeasurement.Line(Powers.POWER_REGISTRY.getResourceKey(power).orElseThrow(),
                    power.getName().toUpperCase() + " - " + getPercent(power_level)
            ));
        }

        paper.set(Registration.LITMUS_MEASUREMENT.get(), new LitmusMeasurement(
                lines,
                crucible.reaction_status,
                crucible.integrity < 85
        ));
    }

    @NotNull
    public static String getPercent(int pow) {
        return pow > 16 ? pow / 16 + "%" : Component.translatable("text.reactive.trace").getString();
    }

}
