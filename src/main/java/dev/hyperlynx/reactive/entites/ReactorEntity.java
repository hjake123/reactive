package dev.hyperlynx.reactive.entites;

import com.mojang.datafixers.util.Either;
import dev.hyperlynx.reactive.ConfigMan;
import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.entites.data.ReactorData;
import dev.hyperlynx.reactive.net.ReactionStatusPayload;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReactorEntity extends Entity implements Reactor {
    public static int MAX_POWER = 10000;

    private static final EntityDataAccessor<ReactorData> SYNCED_REACTOR_DATA = SynchedEntityData.defineId(ReactorEntity.class, Registration.REACTOR_DATA_SERIALIZER.get());

    private static final EntityDataAccessor<Boolean> FORCE_GOLD_SYMBOL = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final String FORCE_GOLD_SYMBOL_KEY = "force_gold_symbol_key";

    private static final EntityDataAccessor<Boolean> USED_CRYSTAL = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final String USED_CRYSTAL_KEY = "has_used_crystal";

    private static final EntityDataAccessor<Integer> ELECTRIC_CHARGE = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.INT);
    private static final String ELECTRIC_CHARGE_KEY = "charge";

    private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(ReactorEntity.class, EntityDataSerializers.INT);
    private static final String LIFESPAN_KEY = "lifespan";

    // Only needs to be used on the server, so no syncing.
    private ReactorData server_reactor_data = new ReactorData(new HashMap<>(), new ArrayList<>());
    private static final String REACTOR_DATA_KEY = "reactor_data";

    private EndCrystal linked_crystal;
    private static final String LINKED_CRYSTAL_KEY = "crystal";

    // Don't need to save either.
    private List<String> render_aliases = new ArrayList<>();
    private AreaMemory area_memory = null;
    private int sync_timer = 10;
    private int react_timer = 0;

    public ReactorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide){
            return;
        }
        if(sync_timer <= 0){
            update();
            sync_timer = 10;
        } else {
            sync_timer--;
        }

        if(react_timer <= 0){
            react((ServerLevel) level());
            react_timer = ConfigMan.COMMON.crucibleTickDelay.get() * 5;
        } else {
            react_timer--;
        }

        if(getEntityData().get(LIFESPAN) <= 0){
            kill();
        }
        getEntityData().set(LIFESPAN, getEntityData().get(LIFESPAN) - 1);
        tryMergeWithNeighbor();
    }

    // Adjacent Reactor Entities may merge into one.
    private void tryMergeWithNeighbor() {
        List<ReactorEntity> nearby_others = this.level().getEntitiesOfClass(ReactorEntity.class,
                this.getBoundingBox().inflate(4.0));
        nearby_others.remove(this);
        for(ReactorEntity neighbor : nearby_others) {
            Vec3 neighbor_pos = neighbor.getPos();
            Vec3 displacement = neighbor_pos.subtract(this.getPos());
            Vec3 step = displacement.normalize().multiply(0.01, 0.01, 0.01);
            this.move(MoverType.SELF, step);
        }

        List<ReactorEntity> touching_others = this.level().getEntitiesOfClass(ReactorEntity.class, this.getBoundingBox());
        touching_others.remove(this);
        for(ReactorEntity touching : touching_others) {
            for(Power power : touching.getPowerMap().keySet()) {
                this.addPower(power, touching.getPowerLevel(power));
            }
            this.setLifespan(Math.max(touching.getLifespan(), this.getLifespan()));
            touching.kill();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SYNCED_REACTOR_DATA, new ReactorData(new HashMap<>(), new ArrayList<>()));
        builder.define(FORCE_GOLD_SYMBOL, false);
        builder.define(USED_CRYSTAL, false);
        builder.define(ELECTRIC_CHARGE, 0);
        builder.define(LIFESPAN, 6000);
    }

    public ReactorData reactorData() {
        if(this.level().isClientSide){
            return this.getEntityData().get(SYNCED_REACTOR_DATA);
        }
        return server_reactor_data;
    }

    private void update() {
        if(this.level().isClientSide){
            throw new UnsupportedOperationException("Can't modify the state of the reaction data on the client!");
        }
        this.getEntityData().set(SYNCED_REACTOR_DATA, server_reactor_data.copy(), true);
    }

    private void update(ReactorData changed) {
        this.server_reactor_data = changed;
        update();
    }

    public void setLifespan(int lifespan) {
        this.getEntityData().set(LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.getEntityData().get(LIFESPAN);
    }

    public void forceGoldSymbol() {
        this.getEntityData().set(FORCE_GOLD_SYMBOL, true);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        var data = this.getEntityData();
        if(compound.contains(REACTOR_DATA_KEY)){
            update(ReactorData.fromTag(compound.getCompound(REACTOR_DATA_KEY)));
        }
        if(compound.contains(FORCE_GOLD_SYMBOL_KEY)){
            data.set(FORCE_GOLD_SYMBOL, compound.getBoolean(FORCE_GOLD_SYMBOL_KEY));
        }
        if(compound.contains(USED_CRYSTAL_KEY)){
            data.set(USED_CRYSTAL, compound.getBoolean(USED_CRYSTAL_KEY));
        }
        if(compound.contains(ELECTRIC_CHARGE_KEY)){
            data.set(ELECTRIC_CHARGE, compound.getInt(ELECTRIC_CHARGE_KEY));
        }
        if(compound.contains(LIFESPAN_KEY)){
            data.set(LIFESPAN, compound.getInt(LIFESPAN_KEY));
        }
        if(this.level() instanceof ServerLevel server && compound.contains(LINKED_CRYSTAL_KEY)) {
            UUID uuid = compound.getUUID(LINKED_CRYSTAL_KEY);
            if (server.getEntity(uuid) instanceof EndCrystal crystal) {
                this.linked_crystal = crystal;
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        var data = this.getEntityData();
        compound.put(REACTOR_DATA_KEY, reactorData().toTag());
        compound.put(FORCE_GOLD_SYMBOL_KEY, ByteTag.valueOf(data.get(FORCE_GOLD_SYMBOL)));
        compound.put(USED_CRYSTAL_KEY, ByteTag.valueOf(data.get(USED_CRYSTAL)));
        compound.put(ELECTRIC_CHARGE_KEY, IntTag.valueOf(data.get(ELECTRIC_CHARGE)));
        compound.put(LIFESPAN_KEY, IntTag.valueOf(data.get(LIFESPAN)));
        if(this.linked_crystal != null) {
            compound.put(LINKED_CRYSTAL_KEY, NbtUtils.createUUID(linked_crystal.getUUID()));
        }
    }

    @Override
    public boolean checkGoldSymbol() {
        return this.getEntityData().get(FORCE_GOLD_SYMBOL) || Reactor.super.checkGoldSymbol();
    }

    @Override
    public List<ReactionStatusEntry> getReactionStatus() {
        return reactorData().statuses();
    }

    @Override
    public void resetReactionStatus() {
        var data = reactorData();
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
    public Vec3 getPos() {
        return this.position();
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
        return reactorData().powers();
    }

    @Override
    public AreaMemory getAreaMemory() {
        if(this.area_memory == null){
            this.area_memory = new AreaMemory(this.getBlockPos());
        }
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
        this.render_aliases.clear();
    }

    @Override
    public void addRenderReaction(String s) {
        this.render_aliases.add(s);
    }

    @Override
    public Iterable<String> getRenderReactions() {
        return this.render_aliases;
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

    public void setPowers(@NotNull Map<Power, Integer> power_map_to_copy) {
        server_reactor_data.powers().clear();
        for(Power power : power_map_to_copy.keySet()){
            server_reactor_data.powers().put(power, power_map_to_copy.get(power));
        }
    }

    @Override
    public ReactionStatusPayload getPayload() {
        return new ReactionStatusPayload(getReactionStatus(), new ReactionStatusPayload.Target(Either.right(getId())));
    }
}
