package dev.hyperlynx.reactive.entites;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.entites.data.ReactorData;
import dev.hyperlynx.reactive.util.AreaMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactorEntity extends Entity implements Reactor {
    EntityDataAccessor<ReactorData> REACTOR_DATA = SynchedEntityData.defineId(ReactorEntity.class, Registration.REACTOR_SERIALIZER.get());
    private final String REACTOR_DATA_KEY = "reactor_data";

    EntityDataAccessor<Boolean> USED_CRYSTAL = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.BOOLEAN);
    private final String USED_CRYSTAL_KEY = "has_used_crystal";

    EntityDataAccessor<Integer> ELECTRIC_CHARGE = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.INT);
    private final String ELECTRIC_CHARGE_KEY = "charge";

    AreaMemory area_memory; // Only needs to be used on the server, so no syncing or saving.

    public ReactorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        area_memory = new AreaMemory(this.getBlockPos());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(REACTOR_DATA, new ReactorData(new HashMap<>(), new ArrayList<>()));
        builder.define(USED_CRYSTAL, false);
        builder.define(ELECTRIC_CHARGE, 0);
    }

    private ReactorData data(){
        return this.getEntityData().get(REACTOR_DATA);
    }

    private void update(ReactorData changed){
        this.getEntityData().set(REACTOR_DATA, changed);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        var data = this.getEntityData();
        update(ReactorData.fromTag(compound.getCompound(REACTOR_DATA_KEY)));
        data.set(USED_CRYSTAL, compound.getBoolean(USED_CRYSTAL_KEY));
        data.set(ELECTRIC_CHARGE, compound.getInt(ELECTRIC_CHARGE_KEY));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        var data = this.getEntityData();
        compound.put(REACTOR_DATA_KEY, data().toTag());
        compound.put(USED_CRYSTAL_KEY, ByteTag.valueOf(data.get(USED_CRYSTAL)));
        compound.put(ELECTRIC_CHARGE_KEY, IntTag.valueOf(data.get(ELECTRIC_CHARGE)));
    }

    @Override
    public List<ReactionStatusEntry> getReactionStatus() {
        return data().statuses();
    }

    @Override
    public void resetReactionStatus() {
        var data = data();
        data.statuses().clear();
        update(data);
    }

    @Override
    public boolean hasUsedCrystalThisCycle() {
        return this.getEntityData().get(USED_CRYSTAL);
    }

    @Override
    public void setUsedCrystalThisCycle(boolean used) {
        this.getEntityData().set(USED_CRYSTAL, used);
    }

    @Override
    public BlockState getBlockState() {
        return this.level().getBlockState(this.getBlockPos());
    }

    @Override
    public BlockPos getBlockPos() {
        return this.blockPosition();
    }

    @Override
    public void setDirty() {
        // NO-OP
    }

    @Override
    public int maxPower() {
        return 1000;
    }

    @Override
    public @NotNull Map<Power, Integer> getPowerMap() {
        return data().powers();
    }

    @Override
    public AreaMemory getAreaMemory() {
        return area_memory;
    }

    @Override
    public Level getLevel() {
        return this.level();
    }

    @Override
    public int getElectricCharge() {
        return this.getEntityData().get(ELECTRIC_CHARGE);
    }

    @Override
    public void setElectricCharge(int i) {
        this.getEntityData().set(ELECTRIC_CHARGE, i);
    }

    @Override
    public Iterable<String> getRenderReactions() {
        return null;
    }

    @Override
    public EndCrystal getLinkedCrystal() {
        return null;
    }

    @Override
    public void setLinkedCrystal(EndCrystal end_crystal) {

    }

    @Override
    public void unlinkCrystal(Level level, BlockPos pos, BlockState state) {

    }

}
