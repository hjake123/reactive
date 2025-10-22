package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.advancements.CriteriaTriggers;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.entities.ThrownReactionFlask;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ReactionFlaskItem extends Item {
    public ReactionFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide()){
            ThrownReactionFlask flask = new ThrownReactionFlask(Registration.THROWN_REACTION_FLASK.get(), level);
            flask.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
            flask.setOwner(player);
            flask.setItem(stack);
            flask.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(flask);
            CriteriaTriggers.THROW_FLASK.trigger((ServerPlayer) player);
        }
        if(!player.getAbilities().instabuild)
            stack.shrink(1);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> hover_text, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, hover_text, tooltipFlag);
        if(!Contents.hasContents(stack)){
            hover_text.add(Component.translatable("text.reactive.random").withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }
        Contents contents = Contents.getFromStack(stack);
        assert contents != null;
        MutableComponent power_readout = Component.empty();
        int counter = 0;
        var powers = contents.powers().keySet();
        for(Power power : powers){
            power_readout.append(Component.literal(power.getName()).withStyle(Style.EMPTY.withColor(power.getColor().hex())));
            counter++;
            if(counter != powers.size()){
                power_readout.append(" + ");
            }
        }

        if(!power_readout.getSiblings().isEmpty()) {
            hover_text.add(power_readout);
        }
        if(contents.electric_charge()) {
            hover_text.add(Component.translatable("text.reactive.charged"));
        }
    }

    public record Contents(Map<Power, Integer> powers, boolean electric_charge) {
        private static final String CONTENT_TAG = "FlaskContents";

        public static boolean hasContents(ItemStack stack) {
            return stack.hasTag() && stack.getOrCreateTag().contains(CONTENT_TAG);
        }

        public static @Nullable Contents getFromStack(ItemStack stack) {
            if(!hasContents(stack)) {
                return null;
            }
            assert stack.getTag() != null;
            CompoundTag tag = stack.getTag().getCompound(CONTENT_TAG);
            var powers = Power.readPowerLevelMap(tag.getList("Powers", Tag.TAG_COMPOUND));
            return new Contents(powers, tag.getBoolean("Charged"));
        }

        public void saveToStack(ItemStack stack) {
            CompoundTag tag = new CompoundTag();
            tag.put("Powers", Power.writePowerLevelMap(powers));
            tag.putBoolean("Charged", electric_charge);
            stack.getOrCreateTag().put(CONTENT_TAG, tag);
        }
    }
}
