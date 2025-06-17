package dev.hyperlynx.reactive.entites;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected @NotNull Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        return getDefaultPassengerAttachmentPoint(this, entity, dimensions.attachments()).add(0, 0.05, 0);
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
    public @Nullable LivingEntity getControllingPassenger() {
        if(this.getFirstPassenger() instanceof LivingEntity driver) {
            return driver;
        }
        return null;
    }

    private static final double MAX_SPEED = 0.25;

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide()) {
            animation_timer++;
            if(this.isVehicle() && this.isControlledByLocalInstance()) {
                LivingEntity riding_entity = this.getControllingPassenger();
                if(riding_entity instanceof LocalPlayer rider) {
                    double vertical_speed = this.getDeltaMovement().y;
                    if(rider.input.up || rider.input.jumping) {
                        vertical_speed = Math.min(MAX_SPEED, vertical_speed + 0.03);
                    }
                    if(rider.input.down) {
                        vertical_speed = Math.max(-MAX_SPEED, vertical_speed - 0.03);
                    }
                    this.setDeltaMovement(0, vertical_speed, 0);
                    this.move(MoverType.PLAYER, this.getDeltaMovement());
                    ReactiveMod.LOGGER.debug("(Mounted) Client position is {}", position().toString());
                }
            } else if(!this.isVehicle()) {
                this.setDeltaMovement(0, 0, 0);
                ReactiveMod.LOGGER.debug("Client position is {}", position().toString());
            }
        } else {
            ReactiveMod.LOGGER.debug("Server position is {}", position().toString());
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
            ReactiveMod.LOGGER.debug("Player mounted");
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        ReactiveMod.LOGGER.debug("Player dismounting");
        return super.getDismountLocationForPassenger(passenger);
    }
}
