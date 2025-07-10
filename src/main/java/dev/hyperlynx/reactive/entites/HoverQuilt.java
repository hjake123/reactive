package dev.hyperlynx.reactive.entites;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.net.HoverQuiltHeightPayload;
import dev.hyperlynx.reactive.net.HoverQuiltVelocityPayload;
import dev.hyperlynx.reactive.registration.ReactiveItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoverQuilt extends VehicleEntity {
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
        return ReactiveItems.PHANTOM_QUILT_ITEM.get();
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
                client_position_lock = true;
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
            this.getControllingPassenger().resetFallDistance();
//            double corner_dist = 0.4;
//            float particle_amount = (float) (Math.abs(this.getDeltaMovement().y / 0.25));
//            if(this.getDeltaMovement().y > 0) {
//                ParticleScribe.drawParticleBox(level(), ParticleTypes.END_ROD, this.getBoundingBox().deflate(0.5).move(0, -0.2, 0), (int) particle_amount * 5);
//            } else if(this.getDeltaMovement().y < 0) {
//                ParticleScribe.drawParticle(level(), ParticleTypes.END_ROD, this.getX() + corner_dist, this.getY(), this.getZ() + corner_dist, 0.2F * particle_amount, 0, 0, 0);
//                ParticleScribe.drawParticle(level(), ParticleTypes.END_ROD, this.getX() + corner_dist, this.getY(), this.getZ() - corner_dist, 0.2F * particle_amount, 0, 0, 0);
//                ParticleScribe.drawParticle(level(), ParticleTypes.END_ROD, this.getX() - corner_dist, this.getY(), this.getZ() + corner_dist, 0.2F * particle_amount, 0, 0, 0);
//                ParticleScribe.drawParticle(level(), ParticleTypes.END_ROD, this.getX() - corner_dist, this.getY(), this.getZ() - corner_dist, 0.2F * particle_amount, 0, 0, 0);
//
//            }
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
        return this.level().collidesWithSuffocatingBlock(null, passenger_top_box);
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
        return this.level().collidesWithSuffocatingBlock(null, passenger_below_box);
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

    public static void handleInputPacket(HoverQuiltVelocityPayload payload, IPayloadContext context) {
        var vehicle = context.player().getVehicle();
        if(vehicle instanceof HoverQuilt quilt) {
            quilt.setDeltaMovement(0, Math.clamp(payload.velocity() + quilt.getDeltaMovement().y, quilt.getMaxDownSpeed(), quilt.getMaxUpSpeed()), 0);
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

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return super.getDismountLocationForPassenger(passenger);
    }

    public static void handleHeightPacket(HoverQuiltHeightPayload payload, IPayloadContext context) {
        var vehicle = context.player().level().getEntity(payload.id());
        if(vehicle instanceof HoverQuilt quilt) {
            quilt.client_position_lock = false;
            quilt.setPos(quilt.getX(), payload.height(), quilt.getZ());
            quilt.client_position_lock = true;
            quilt.setDeltaMovement(0, 0, 0);
            quilt.setOldPosAndRot();
            quilt.lerpPositionAndRotationStep(1, quilt.getX(), payload.height(), quilt.getZ(), quilt.getYRot(), quilt.getXRot());
        }
    }

    @Override
    public void setPos(double x, double y, double z) {
        if(client_position_lock) {
            return;
        }
        super.setPos(x, y, z);
    }
}
