package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.alchemy.Power;
import com.hyperlynx.reactive.be.CrucibleBlockEntity;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.parser.TagElement;
import java.util.ArrayList;
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
    public InteractionResult useOn(UseOnContext context) {
        if(!context.getLevel().getBlockState(context.getClickedPos()).is(Registration.CRUCIBLE.get())){
            return InteractionResult.PASS;
        }

        if(context.getItemInHand().hasTag())
            return InteractionResult.CONSUME;

        CrucibleBlockEntity crucible = (CrucibleBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
        if(crucible == null || crucible.getTotalPowerLevel() == 0) {
            return InteractionResult.PASS;
        }

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

        if(!context.getItemInHand().hasTag())
            context.getItemInHand().setTag(new CompoundTag());
        context.getItemInHand().getTag().put(TAG_MEASUREMENT, measurements);

        return InteractionResult.SUCCESS;
    }
}
