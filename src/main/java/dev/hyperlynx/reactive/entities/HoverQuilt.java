package dev.hyperlynx.reactive.entities;

import dev.hyperlynx.reactive.net.quilt.HoverQuiltHeightMessage;
import dev.hyperlynx.reactive.net.quilt.HoverQuiltVelocityMessage;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class HoverQuilt extends Entity {
    public final AnimationState hovering = new AnimationState();
    public long animation_timer = 0;
    private boolean ridden_last_tick = false;
    private int position_force_timer = 0;
    private boolean client_position_lock = false;

    public HoverQuilt(EntityType<?> entityType, Level level) {
        super(entityType, level);
        hovering.start(0);
        this.setInvulnerable(false);
    }

    @Override
    protected void defineSynchedData() {

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
    public double getPassengersRidingOffset() {
        return 0.05;
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
            // Someone else is riding it, so we need to turn the lock off.
            // No one is riding it, so it should not move.
            if(this.isVehicle() && this.isControlledByLocalInstance()) {
                // We're riding it.
                LivingEntity riding_entity = this.getControllingPassenger();
                if(riding_entity instanceof LocalPlayer rider) {
                    float velocity = 0.0F;
                    if(rider.input.up || rider.input.jumping) {
                        velocity = 0.03F;
                    }
                    if(rider.input.down) {
                        velocity = -0.03F;
                    }
                    PacketDistributor.sendToServer(new HoverQuiltVelocityPayload(velocity));
                }
                client_position_lock = false;
            } else {
                client_position_lock = !this.isVehicle();
            }
        } else if(!this.isVehicle()) {
            this.setDeltaMovement(0, 0, 0);
            if(ridden_last_tick) {
                position_force_timer = 10;
            }
            if(position_force_timer > 0) {
                PacketDistributor.sendToPlayersTrackingEntity(this, new HoverQuiltHeightPayload(this.getId(), this.getY()));
                position_force_timer--;
            }
            ridden_last_tick = false;
        } else {
            ridden_last_tick = true;
            Objects.requireNonNull(this.getControllingPassenger()).resetFallDistance();
        }
        if(this.isHittingRidersHead()) {
            this.setDeltaMovement(0, Math.min(-0.05, -this.getDeltaMovement().y), 0);
        } else if(this.isHittingRidersButt()) {
            this.setDeltaMovement(0, Math.max(0.05, -this.getDeltaMovement().y), 0);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private boolean isHittingRidersHead() {
        if(!this.isVehicle()) {
            return false;
        }
        Entity passenger = this.getControllingPassenger();
        if(passenger == null) {
            return false;
        }
        AABB passenger_hitbox = passenger.getBoundingBox();
        double passenger_width = passenger_hitbox.getXsize();
        AABB passenger_top_box = new AABB(passenger_hitbox.getMaxPosition().subtract(passenger_width, 1, passenger_width), passenger_hitbox.getMaxPosition().add(0, 0.1, 0));
        return !this.level().noBlockCollision(null, passenger_top_box);
    }

    private boolean isHittingRidersButt() {
        if(!this.isVehicle()) {
            return false;
        }
        Entity passenger = this.getControllingPassenger();
        if(passenger == null) {
            return false;
        }
        AABB passenger_hitbox = passenger.getBoundingBox();
        double passenger_width = passenger_hitbox.getXsize();
        AABB passenger_below_box = new AABB(passenger_hitbox.getMinPosition(), passenger_hitbox.getMinPosition().add(passenger_width, 1, passenger_width));
        return !this.level().noBlockCollision(null, passenger_below_box);
    }

    private double getMaxUpSpeed() {
        double max_world_height = this.level().getMaxBuildHeight();
        double current_height = this.position().y;
        if(current_height > max_world_height) {
            if(current_height - max_world_height > 10) {
                return 0;
            }
            return MAX_SPEED * (1 / (current_height - max_world_height + 1));
        }
        return MAX_SPEED;
    }

    private double getMaxDownSpeed() {
        double min_world_height = this.level().getMinBuildHeight();
        double current_height = this.position().y;
        if(current_height < min_world_height) {
            if(current_height - min_world_height < -5) {
                return 0;
            }
            return -MAX_SPEED * (1 / (min_world_height - current_height + 1));
        }
        return -MAX_SPEED;
    }

    public static void handleInputPacket(HoverQuiltVelocityMessage payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if(context.get().getSender() == null) {
                return;
            }
            var vehicle = Objects.requireNonNull(context.get().getSender()).getVehicle();
            if(vehicle instanceof HoverQuilt quilt) {
                quilt.setDeltaMovement(0, Math.clamp(payload.velocity() + quilt.getDeltaMovement().y, quilt.getMaxDownSpeed(), quilt.getMaxUpSpeed()), 0);
            }
        });

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

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(@NotNull LivingEntity passenger) {
        return super.getDismountLocationForPassenger(passenger);
    }

    public static void handleHeightPacket(HoverQuiltHeightMessage payload, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            var vehicle = context.get().getSender().level().getEntity(payload.id());
            if(vehicle instanceof HoverQuilt quilt) {
                quilt.client_position_lock = false;
                quilt.setPos(quilt.getX(), payload.height(), quilt.getZ());
                quilt.client_position_lock = true;
                quilt.setDeltaMovement(0, 0, 0);
                quilt.setOldPosAndRot();
                quilt.lerpTo(quilt.getX(), payload.height(), quilt.getZ(), quilt.getYRot(), quilt.getXRot(), 1, false);
            }
        });
    }

    @Override
    public void setPos(double x, double y, double z) {
        if(client_position_lock) {
            return;
        }
        super.setPos(x, y, z);
    }
}
