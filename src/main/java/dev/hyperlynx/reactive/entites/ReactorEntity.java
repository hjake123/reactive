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
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReactorEntity extends Entity implements Reactor {
    public static int MAX_POWER = 10000;

    private static final EntityDataAccessor<ReactorData> REACTOR_DATA = SynchedEntityData.defineId(ReactorEntity.class, Registration.REACTOR_DATA_SERIALIZER.get());
    private static final String REACTOR_DATA_KEY = "reactor_data";

    private static final EntityDataAccessor<Boolean> USED_CRYSTAL = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final String USED_CRYSTAL_KEY = "has_used_crystal";

    private static final EntityDataAccessor<Integer> ELECTRIC_CHARGE = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.INT);
    private static final String ELECTRIC_CHARGE_KEY = "charge";

    // Only needs to be used on the server, so no syncing.
    private EndCrystal linked_crystal;
    private static final String LINKED_CRYSTAL_KEY = "crystal";

    // Don't need to save this either.
    private AreaMemory area_memory;

    public ReactorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        area_memory = new AreaMemory(this.getBlockPos());
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(REACTOR_DATA, new ReactorData(new HashMap<>(), new ArrayList<>(), new ArrayList<>()));
        builder.define(USED_CRYSTAL, false);
        builder.define(ELECTRIC_CHARGE, 0);
    }

    public ReactorData data(){
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
        if(this.level() instanceof ServerLevel server) {
            if(compound.contains(LINKED_CRYSTAL_KEY)) {
                UUID uuid = compound.getUUID(LINKED_CRYSTAL_KEY);
                if(server.getEntity(uuid) instanceof EndCrystal crystal) {
                    this.linked_crystal = crystal;
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        var data = this.getEntityData();
        compound.put(REACTOR_DATA_KEY, data().toTag());
        compound.put(USED_CRYSTAL_KEY, ByteTag.valueOf(data.get(USED_CRYSTAL)));
        compound.put(ELECTRIC_CHARGE_KEY, IntTag.valueOf(data.get(ELECTRIC_CHARGE)));
        if(this.linked_crystal != null) {
            compound.put(LINKED_CRYSTAL_KEY, NbtUtils.createUUID(linked_crystal.getUUID()));
        }
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
        return MAX_POWER;
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
    public void clearRenderReactions() {
        data().render_aliases().clear();
    }

    @Override
    public void addRenderReaction(String s) {
        data().render_aliases().add(s);
    }

    @Override
    public Iterable<String> getRenderReactions() {
        return data().render_aliases();
    }

    @Override
    public EndCrystal getLinkedCrystal() {
        return linked_crystal;
    }

    @Override
    public void setLinkedCrystal(EndCrystal end_crystal) {
        this.linked_crystal = end_crystal;
    }

    @Override
    public void unlinkCrystal(Level level, BlockPos pos, BlockState state) {
        linked_crystal.setBeamTarget(null);
        linked_crystal = null;
    }
}
