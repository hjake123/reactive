package com.hyperlynx.reactive.be;

import com.hyperlynx.reactive.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
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
// Data is placed by the DisplacedBlock::displaceWithChain method
// Data is read by the DisplacedBlock::tick method
public class DisplacedBlockEntity extends BlockEntity {
    private BlockState self_state;
    public BlockPos chain_target;
    public int depth = 0;
    public boolean first_tick = true; // Not persisted, set to false after the first tick. Used to offset for the cool flood effect after reloading.
    private CompoundTag unresolved_self_state; // If the level doesn't exist when the block entity loads, the self state can't be determined, so it is stored here temporarily.

    public static final String BLOCK_STATE_TAG = "DisplacedBlockState";
    public static final String CHAIN_TARGET_TAG = "ChainTargetBlockPos";
    public static final String DEPTH_TAG = "ChainDepth";


    public DisplacedBlockEntity(BlockPos pos, BlockState state) {
        super(Registration.DISPLACED_BLOCK_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag main_tag) {
        super.saveAdditional(main_tag);
        if(getSelfState() == null)
            main_tag.put(BLOCK_STATE_TAG, unresolved_self_state);
        else
            main_tag.put(BLOCK_STATE_TAG, NbtUtils.writeBlockState(getSelfState()));
        if(chain_target != null)
            main_tag.put(CHAIN_TARGET_TAG, NbtUtils.writeBlockPos(chain_target));
        main_tag.put(DEPTH_TAG, IntTag.valueOf(depth));
    }

    @Override
    public void load(@NotNull CompoundTag main_tag) {
        super.load(main_tag);
        unresolved_self_state = main_tag.getCompound(BLOCK_STATE_TAG);
        if(main_tag.contains(CHAIN_TARGET_TAG))
            chain_target = NbtUtils.readBlockPos(main_tag.getCompound(CHAIN_TARGET_TAG));
        if(main_tag.contains(DEPTH_TAG))
            depth = main_tag.getInt(DEPTH_TAG);
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

    public BlockState getSelfState() {
        if(self_state == null) {
            if (Minecraft.getInstance().level == null) {
                return null;
            }
            setSelfState(NbtUtils.readBlockState(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), unresolved_self_state));
        }
        return self_state;
    }

    public void setSelfState(BlockState self_state) {
        this.self_state = self_state;
    }
}
