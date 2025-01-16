package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class GatewayBlockEntity extends TheEndPortalBlockEntity {
    private int tick_count;
    public GlobalPos target;
    private final String TARGET_POS_TAG = "Target";
    private final String TARGET_DIMENSION_TAG = "Dimension";

    public GatewayBlockEntity(BlockPos pos, BlockState blockState) {
        super(Registration.GATEWAY_BE.get(), pos, blockState);
    }

    @Override
    public boolean shouldRenderFace(Direction face) {
        return true;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, T gateway) {
        ((GatewayBlockEntity) gateway).tick_count++;
    }

    public float totalTick(float partialTick) {
        return (float) tick_count + partialTick;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(this.target == null)
            return;
        tag.put(TARGET_POS_TAG, NbtUtils.writeBlockPos(target.pos()));
        tag.put(TARGET_DIMENSION_TAG, StringTag.valueOf(String.valueOf(target.dimension().location())));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Optional<BlockPos> pos = NbtUtils.readBlockPos(tag, TARGET_POS_TAG);
        if(pos.isEmpty())
            return;
        ResourceLocation location = ResourceLocation.parse(tag.get(TARGET_DIMENSION_TAG).getAsString());
        target = GlobalPos.of(ResourceKey.create(Registries.DIMENSION, location), pos.get());
    }
}
