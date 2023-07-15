package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.alchemy.rxn.Reaction;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import com.hyperlynx.reactive.blocks.CrucibleBlock;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
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
    private List<Component> buildMeasurmentText(ItemStack stack){
        List<Component> text = new ArrayList<>();
        if(!stack.hasTag())
            return text;

        ListTag measurements = stack.getTag().getList(TAG_MEASUREMENT, Tag.TAG_COMPOUND);
        for(Tag tag : measurements){
            text.add(Component.literal(((CompoundTag) tag).getString("value")));
        }

        if(measurements.isEmpty()){
            text.add(Component.translatable("text.reactive.measurement_empty"));
        }

        StringTag reaction_status = (StringTag) stack.getTag().get(TAG_STATUS);
        switch(Reaction.Status.valueOf(reaction_status.getAsString())){
            case STABLE -> text.add(Component.translatable("text.reactive.stable"));
            case VOLATILE -> text.add(Component.translatable("text.reactive.single_power_reaction_missing_condition"));
            case POWER_TOO_WEAK -> text.add(Component.translatable("text.reactive.power_too_weak"));
            case MISSING_STIMULUS -> text.add(Component.translatable("text.reactive.multi_power_reaction_missing_condition"));
            case INHIBITED -> text.add(Component.translatable("text.reactive.inhibited"));
            case REACTING -> text.add(Component.translatable("text.reactive.reacting"));
        }
        return text;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> hover_text, TooltipFlag tooltip_flag) {
        super.appendHoverText(stack, level, hover_text, tooltip_flag);
        hover_text.addAll(buildMeasurmentText(stack));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(player.isCrouching() && level.isClientSide){
            if(!player.getItemInHand(hand).hasTag())
                return InteractionResultHolder.pass(player.getItemInHand(hand));

            player.sendSystemMessage(Component.translatable("text.reactive.measurement_header"));
            for(Component line : buildMeasurmentText(player.getItemInHand(hand))){
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

        for(Power p : crucible.getPowerMap().keySet()){
            if(crucible.getPowerLevel(p) == 0)
                continue;

            String measurement = p.getName().toUpperCase() + " - ";

            if(crucible.getPowerLevel(p) < 500){
                measurement += "LOW";
            }else if(500 <= crucible.getPowerLevel(p) && crucible.getPowerLevel(p) < 1000){
                measurement += "MEDIUM";
            }else{
                measurement += "HIGH";
            }

            CompoundTag mt = new CompoundTag();
            mt.putString("value", measurement);
            measurements.add(mt);
        }

        if(!paper.hasTag())
            paper.setTag(new CompoundTag());
        paper.getTag().put(TAG_MEASUREMENT, measurements);
        paper.getTag().put(TAG_STATUS, StringTag.valueOf(crucible.reaction_status.toString()));

    }
}
