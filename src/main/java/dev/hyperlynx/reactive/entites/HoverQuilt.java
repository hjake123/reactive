package dev.hyperlynx.reactive.entites;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HoverQuilt extends VehicleEntity {
    public final AnimationState hovering = new AnimationState();
    public long animation_timer = 0;

    public HoverQuilt(EntityType<?> entityType, Level level) {
        super(entityType, level);
        hovering.start(0);
        this.setInvulnerable(false);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }


    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return super.isInvulnerable();
    }

    @Override
    protected @NotNull Item getDropItem() {
        return Registration.PHANTOM_QUILT_ITEM.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide()) {
            animation_timer++;
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult super_interaction_result = super.interact(player, hand);
        if (super_interaction_result != InteractionResult.PASS) {
            return super_interaction_result;
        }
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }
}
