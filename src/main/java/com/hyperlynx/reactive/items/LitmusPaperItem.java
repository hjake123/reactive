package com.hyperlynx.reactive.items;

import ca.weblite.objc.Client;
import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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

import java.util.List;

public class LitmusPaperItem extends Item {
    public static final String TAG_MEASUREMENT = "Measurement";
    public LitmusPaperItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> hover_text, TooltipFlag tooltip_flag) {
        super.appendHoverText(stack, level, hover_text, tooltip_flag);

        if(!stack.hasTag())
            return;

        ListTag measurements = stack.getTag().getList(TAG_MEASUREMENT, Tag.TAG_COMPOUND);
        for(Tag tag : measurements){
            hover_text.add(Component.literal(((CompoundTag) tag).getString("value")));
        }
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(player.isCrouching() && level.isClientSide){
            ListTag measurements = player.getItemInHand(hand).getTag().getList(TAG_MEASUREMENT, Tag.TAG_COMPOUND);
            for(Tag tag : measurements){
                player.sendSystemMessage(Component.literal(((CompoundTag) tag).getString("value")));
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(!context.getLevel().getBlockState(context.getClickedPos()).is(Registration.CRUCIBLE.get())){
            return InteractionResult.PASS;
        }

        CrucibleBlockEntity crucible = (CrucibleBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
        if(crucible == null || crucible.getTotalPowerLevel() == 0) {
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
    }
}
