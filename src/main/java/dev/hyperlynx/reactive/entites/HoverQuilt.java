package dev.hyperlynx.reactive.entites;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class HoverQuilt extends Entity {
    public final AnimationState hovering = new AnimationState();
    public long animation_timer = 0;

    public HoverQuilt(EntityType<?> entityType, Level level) {
        super(entityType, level);
        hovering.start(0);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

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
}
