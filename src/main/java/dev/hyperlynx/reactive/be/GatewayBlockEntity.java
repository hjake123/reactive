package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

public class GatewayBlockEntity extends TheEndPortalBlockEntity {
    private int tick_count;
    public GlobalPos target;
    private int warp_cooldown = 0;
    private final String TARGET_POS_TAG = "Target";
    private final String TARGET_DIMENSION_TAG = "Dimension";
    private final String COOLDOWN_TAG = "Cooldown";

    public GatewayBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.GATEWAY_BE.get(), pos, blockState);
        Random random = new Random(pos.hashCode());
        tick_count = random.nextInt(0, 12000);
    }

    @Override
    public boolean shouldRenderFace(Direction face) {
        return true;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T t) {
        if(!(t instanceof GatewayBlockEntity gateway)){
            return;
        }
        if(level.isClientSide()){
            gateway.tick_count++;
        } else if(gateway.isOnCooldown()){
            gateway.warp_cooldown--;
        }
    }

    public float totalTick(float partialTick) {
        return (float) tick_count + partialTick;
    }

    public void setCooldown(int cooldown){
        this.warp_cooldown = cooldown;
    }

    public boolean isOnCooldown(){
        return this.warp_cooldown > 0;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if(this.target == null)
            return;
        tag.put(TARGET_POS_TAG, NbtUtils.writeBlockPos(target.pos()));
        tag.put(TARGET_DIMENSION_TAG, StringTag.valueOf(String.valueOf(target.dimension().location())));
        tag.put(COOLDOWN_TAG, IntTag.valueOf(warp_cooldown));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        BlockPos pos;
        try {
            pos = NbtUtils.readBlockPos(tag.getCompound(TARGET_POS_TAG));
        }catch(ClassCastException exception){
            return;
        }
        ResourceLocation location = new ResourceLocation(tag.get(TARGET_DIMENSION_TAG).getAsString());
        target = GlobalPos.of(ResourceKey.create(Registries.DIMENSION, location), pos);
        warp_cooldown = tag.getInt(COOLDOWN_TAG);
    }
}
