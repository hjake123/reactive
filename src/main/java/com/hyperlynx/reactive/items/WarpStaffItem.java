package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.fx.particles.ParticleScribe;
import com.hyperlynx.reactive.util.BeamHelper;
import com.hyperlynx.reactive.util.ConfigMan;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class WarpStaffItem extends StaffItem{
    public static final String TAG_BOUND_ENTITY_UUID = "BoundEntityUUID";
    public static final String TAG_CUSTOM_MODEL_DATA = "CustomModelData";
    public static final String TAG_TUTORIAL_FINISHED = "TutorialFinished";
    public static final String TAG_BOUND_ENTITY_ID = "BoundEntityId";
    private static final int RANGE_SQUARED = 4096;

    public WarpStaffItem(Block block, Properties props, Item repair_item) {
        super(block, props, null, false, repair_item);
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
        return stack.hasTag() && stack.getTag().contains(TAG_BOUND_ENTITY_UUID);
    }

    public @Nullable Entity getBoundEntity(Level level, ItemStack stack){
        if(!stack.hasTag())
            return null;
        if(!(level instanceof ServerLevel server))
            return level.getEntity(stack.getTag().getInt(TAG_BOUND_ENTITY_ID));
        return server.getEntity(stack.getTag().getUUID(TAG_BOUND_ENTITY_UUID));
    }

    public static void tryShowTutorial(Player user, ItemStack stack){
        if(!stack.hasTag() || !stack.getTag().contains(TAG_TUTORIAL_FINISHED)){
            user.displayClientMessage(Component.translatable("message.reactive.warp_staff_tutorial"), true);
        }
    }

    private void zap(Player user, Vec3 target, ParticleOptions opt){
        ParticleScribe.drawParticleZigZag(user.level, opt,
                user.getEyePosition().x, user.getEyePosition().y - 0.4, user.getEyePosition().z,
                target.x, target.y, target.z, 5, 3, 0.4);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> hover_text, TooltipFlag tooltip_flag) {
        super.appendHoverText(stack, level, hover_text, tooltip_flag);
        if(level != null && hasBoundEntity(stack)){
            Entity bound = getBoundEntity(level, stack);
            if(bound == null){
                hover_text.add(Component.translatable("tooltip.reactive.no_entity_bound"));
                return;
            }
            hover_text.add(Component.translatable("tooltip.reactive.entity_bound")
                    .append(bound.hasCustomName() ? bound.getCustomName() : bound.getName()));
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
            if(bound != null && bound.getPosition(0).distanceToSqr(wielder.getPosition(0)) < RANGE_SQUARED) {
                ParticleScribe.drawParticleRing(level, ParticleTypes.REVERSE_PORTAL, bound.position(), 0, 0.5, 4);
                stack.getTag().put(TAG_CUSTOM_MODEL_DATA, IntTag.valueOf(1));
            }
            else {
                stack.getTag().remove(TAG_BOUND_ENTITY_UUID);
            }
            // Update the internal id of the entity for syncing to the client.
            if(stack.getTag().contains(TAG_BOUND_ENTITY_ID))
                stack.getTag().remove(TAG_BOUND_ENTITY_ID);
            if(bound != null)
                stack.getTag().put(TAG_BOUND_ENTITY_ID, IntTag.valueOf(bound.getId()));
        }else{
            stack.getTag().put(TAG_CUSTOM_MODEL_DATA, IntTag.valueOf(0));
            if(stack.getTag().contains(TAG_BOUND_ENTITY_ID))
                stack.getTag().remove(TAG_BOUND_ENTITY_ID);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        int range = 12;
        var blockHit = BeamHelper.playerRayTrace(user.level, user, ClipContext.Fluid.NONE, ClipContext.Block.OUTLINE, range);
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
            if (!hasBoundEntity(stack) && entityHit != null) {
                // Items just get yoinked.
                if(entityHit.getEntity() instanceof ItemEntity || entityHit.getEntity() instanceof ExperienceOrb){
                    entityHit.getEntity().teleportTo(user.position().x, user.position().y, user.position().z);
                    stack.hurtAndBreak(1, user, (unused) -> {});
                    return InteractionResultHolder.success(stack);
                }
                // Select the entity.
                if (!ConfigMan.COMMON.doNotTeleport.get().contains(entityHit.getEntity().getEncodeId()) && !(entityHit.getEntity() instanceof Player)) {
                    if(!stack.hasTag()){
                        stack.setTag(new CompoundTag());
                    }
                    if(entityHit.getEntity() instanceof EnderMan man){ // Trying to warp an Enderman breaks the staff momentarily.
                        man.hurt(DamageSource.playerAttack(user), 1);
                        man.setBeingStaredAt();
                        user.getCooldowns().addCooldown(stack.getItem(), 100);
                    }else{
                        stack.getTag().put(TAG_BOUND_ENTITY_UUID, NbtUtils.createUUID(entityHit.getEntity().getUUID()));
                        stack.getTag().put(TAG_TUTORIAL_FINISHED, IntTag.valueOf(1));
                    }
                    zap(user, beam_end, ParticleTypes.ENCHANTED_HIT);
                    level.playSound(null, beam_end.x, beam_end.y, beam_end.z, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS,
                            0.6F, 1.0F + user.level.random.nextFloat()*0.2F);
                }
                stack.hurtAndBreak(1, user, (unused) -> {});
                return InteractionResultHolder.success(stack);
            } else if (hasBoundEntity(stack)) {
                // Teleport the bound entity.
                Entity bound = getBoundEntity(level, stack);
                if (bound != null) {
                    bound.teleportTo(beam_end.x, beam_end.y, beam_end.z);
                    zap(user, beam_end, ParticleTypes.REVERSE_PORTAL);
                    level.playSound(null, bound, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                }
                stack.getTag().remove(TAG_BOUND_ENTITY_UUID);
                stack.hurtAndBreak(1, user, (unused) -> {
                });
                return InteractionResultHolder.success(stack);
            } else {
                tryShowTutorial(user, stack);
            }
        }
        return InteractionResultHolder.fail(stack);
    }
}
