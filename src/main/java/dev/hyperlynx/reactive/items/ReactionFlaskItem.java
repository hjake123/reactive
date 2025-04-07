package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.entites.ThrownReactionFlask;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
}
