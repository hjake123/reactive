package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.components.BoundEntity;
import com.hyperlynx.reactive.components.ReactiveDataComponents;
import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.util.BeamHelper;
import com.hyperlynx.reactive.ConfigMan;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Objects;

public class WarpStaffItem extends StaffItem{
    public WarpStaffItem(Block block, Properties props, Item repair_item) {
        super(block, props, null, false, 1, repair_item);
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int ticks) {
        // NO-OP
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 0;
    }

    public static boolean hasBoundEntity(ItemStack stack){
        return stack.has(ReactiveDataComponents.BOUND_ENTITY);
    }

    public @Nullable Entity getBoundEntity(Level level, ItemStack stack){
        if(!stack.has(ReactiveDataComponents.BOUND_ENTITY))
            return null;
        if(!(level instanceof ServerLevel server))
            return null;
        return server.getEntity(Objects.requireNonNull(stack.get(ReactiveDataComponents.BOUND_ENTITY)).uuid());
    }

    public static void tryShowTutorial(Player user, ItemStack stack){
        if(!stack.has(ReactiveDataComponents.TUTORIAL_DONE)){
            user.displayClientMessage(Component.translatable("message.reactive.warp_staff_tutorial"), true);
        }
    }

    private void zap(Player user, Vec3 target, ParticleOptions opt){
        ParticleScribe.drawParticleZigZag(user.level(), opt,
                user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                target.x, target.y, target.z, 5, 3, 0.4);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> hover_text, TooltipFlag tooltip_flag) {
        super.appendHoverText(stack, context, hover_text, tooltip_flag);
        if(hasBoundEntity(stack)){
            hover_text.add(Component.translatable("tooltip.reactive.entity_bound")
                    .append(stack.get(ReactiveDataComponents.BOUND_ENTITY).name()));
        }else{
            hover_text.add(Component.translatable("tooltip.reactive.no_entity_bound"));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity wielder, int tick, boolean unknown) {
        if(!(level instanceof ServerLevel))
            return;
        if(hasBoundEntity(stack)){
            Entity bound = getBoundEntity(level, stack);
            // Draw the particles and update the model data or unbind invalid or too distant entities.
            if(bound != null) {
                ParticleScribe.drawParticleRing(level, ParticleTypes.REVERSE_PORTAL, bound.position(), 0, 0.5, 4);
                stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
            }else{
                stack.remove(ReactiveDataComponents.BOUND_ENTITY);
            }

        }else{
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        EquipmentSlot slot = LivingEntity.getSlotForHand(hand);
        if(onLastDurability(stack))
            return InteractionResultHolder.fail(stack);

        int range = 12;
        var blockHit = BeamHelper.playerRayTrace(user.level(), user, ClipContext.Fluid.NONE, ClipContext.Block.OUTLINE, range);
        var blockHitPos = blockHit.getLocation();
        var start = user.getEyePosition();
        var end = start.add(user.getLookAngle().scale(range));
        var entityHit = ProjectileUtil.getEntityHitResult(
                user, start, end, new AABB(start, end), Objects::nonNull, Double.MAX_VALUE
        );

        // Check which is closer
        Vec3 beam_end;
        if (entityHit == null) {
            beam_end = blockHitPos;
        } else if (entityHit.getLocation().distanceToSqr(start) < blockHitPos.distanceToSqr(start)) {
            beam_end = entityHit.getLocation();
        } else {
            beam_end = blockHitPos;
        }

        if(user instanceof ServerPlayer) {
            if (!hasBoundEntity(stack) && entityHit != null && entityHit.getEntity() instanceof LivingEntity) {
                // Items just get yoinked.
                if(entityHit.getEntity() instanceof ItemEntity || entityHit.getEntity() instanceof ExperienceOrb){
                    entityHit.getEntity().teleportTo(user.position().x, user.position().y, user.position().z);
                    stack.hurtAndBreak(1, user, slot);
                    return InteractionResultHolder.success(stack);
                }
                // Select the entity.
                if (!ConfigMan.COMMON.doNotTeleport.get().contains(entityHit.getEntity().getEncodeId()) && !(entityHit.getEntity() instanceof Player)) {
                    Entity entity = entityHit.getEntity();
                    if(entity instanceof EnderMan man){ // Trying to warp an Enderman breaks the staff momentarily.
                        man.hurt(user.damageSources().magic(), 1);
                        man.setBeingStaredAt();
                        user.getCooldowns().addCooldown(stack.getItem(), 100);
                    }else{
                        String name = entity.hasCustomName() ? entity.getCustomName().getString() : entity.getName().getString();
                        stack.set(ReactiveDataComponents.BOUND_ENTITY, new BoundEntity(name, entityHit.getEntity().getUUID()));
                        stack.set(ReactiveDataComponents.TUTORIAL_DONE, Unit.INSTANCE);
                    }
                    zap(user, beam_end, ParticleTypes.ENCHANTED_HIT);
                    level.playSound(null, beam_end.x, beam_end.y, beam_end.z, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS,
                            0.6F, 1.0F + user.level().random.nextFloat()*0.2F);
                }
                stack.hurtAndBreak(1, user, slot);
                return InteractionResultHolder.success(stack);
            } else if (hasBoundEntity(stack)) {
                // Teleport the bound entity.
                Entity bound = getBoundEntity(level, stack);
                if (bound != null) {
                    bound.teleportTo(beam_end.x, beam_end.y, beam_end.z);
                    zap(user, beam_end, ParticleTypes.REVERSE_PORTAL);
                    level.playSound(null, bound, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                }
                stack.remove(ReactiveDataComponents.BOUND_ENTITY);
                stack.hurtAndBreak(1, user, slot);
                return InteractionResultHolder.success(stack);
            } else {
                tryShowTutorial(user, stack);
            }
        }
        return InteractionResultHolder.fail(stack);
    }
}
