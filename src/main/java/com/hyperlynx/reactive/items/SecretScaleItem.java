package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SecretScaleItem extends Item {
    public SecretScaleItem(Properties props) {
        super(props);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        entity.setNoGravity(true);
        if(entity.tickCount % 4 == 1)
            entity.level().addParticle(ParticleTypes.END_ROD, entity.getX(), entity.getY()+0.15, entity.getZ(), 0,0,0);
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // When the player shift-clicks on a boat, it becomes antigravity and the item is used up.
        if(!player.isCrouching())
            return InteractionResultHolder.pass(player.getItemInHand(hand));;

        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(player.entityInteractionRange()));
        EntityHitResult boatHit = ProjectileUtil.getEntityHitResult(
                player, start, end, new AABB(start, end), e -> e instanceof Boat, Double.MAX_VALUE
        );
        if(boatHit == null)
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        if(boatHit.getEntity().isNoGravity())
            return InteractionResultHolder.pass(player.getItemInHand(hand));

        boatHit.getEntity().setNoGravity(true);
        if(!player.isCreative())
            player.getItemInHand(hand).shrink(1);
        ParticleScribe.drawParticleRing(level, ParticleTypes.END_ROD, boatHit.getEntity().getOnPos(), 1.3, 1.4, 10);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
