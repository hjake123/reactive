package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// Saves data about a block that has been made intangible.
// Data is placed by the DisplacedBlock::displace method
// Data is read by the DisplacedBlock::tick method
public class DisplacedBlockEntity extends BlockEntity {
    public BlockState self_state;
    public BlockPos chain_target;

    public static final String BLOCK_STATE_TAG = "DisplacedBlockState";
    public static final String CHAIN_TARGET_TAG = "ChainTargetBlockPos";

    public DisplacedBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.DISPLACED_BLOCK_BE.get(), pos, state);
        self_state = Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag main_tag) {
        super.saveAdditional(main_tag);
        main_tag.put(BLOCK_STATE_TAG, NbtUtils.writeBlockState(self_state));
        if(chain_target != null)
            main_tag.put(CHAIN_TARGET_TAG, NbtUtils.writeBlockPos(chain_target));
    }

    @Override
    public void load(@NotNull CompoundTag main_tag) {
        super.load(main_tag);
        self_state = NbtUtils.readBlockState(main_tag.getCompound(BLOCK_STATE_TAG));
        if(main_tag.contains(CHAIN_TARGET_TAG))
            chain_target = NbtUtils.readBlockPos(main_tag.getCompound(CHAIN_TARGET_TAG));
    }

    // Sync to the client for the sake of pickblock
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

}
