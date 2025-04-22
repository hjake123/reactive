package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.components.ReactionFlaskContents;
import dev.hyperlynx.reactive.entites.ThrownReactionFlask;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class ReactionFlaskItem extends Item implements ProjectileItem {
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
        }
        stack.consume(1, player);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        ThrownReactionFlask flask = new ThrownReactionFlask(Registration.THROWN_REACTION_FLASK.get(), level);
        flask.setPos(new Vec3(pos.x(), pos.y(), pos.z()));
        flask.setItem(stack);
        return flask;
    }

    @Override
    public DispenseConfig createDispenseConfig() {
        return ProjectileItem.super.createDispenseConfig();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> hover_text, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, hover_text, tooltipFlag);
        if(!stack.has(Registration.REACTION_FLASK_CONTENTS.get())){
            hover_text.add(Component.translatable("text.reactive.random").withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }
        ReactionFlaskContents contents = stack.get(Registration.REACTION_FLASK_CONTENTS.get());
        MutableComponent power_readout = Component.empty();
        int counter = 0;
        var powers = contents.powers().keySet();
        for(Power power : powers){
            power_readout.append(Component.literal(power.getName()).withColor(power.getColor().hex()));
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
}
