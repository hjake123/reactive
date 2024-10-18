package dev.hyperlynx.reactive.be;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
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
    protected void saveAdditional(@NotNull CompoundTag main_tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(main_tag, registries);
        if(getSelfState() == null)
            main_tag.put(BLOCK_STATE_TAG, unresolved_self_state);
        else
            main_tag.put(BLOCK_STATE_TAG, NbtUtils.writeBlockState(getSelfState()));
        if(chain_target != null)
            main_tag.put(CHAIN_TARGET_TAG, NbtUtils.writeBlockPos(chain_target));
        main_tag.put(DEPTH_TAG, IntTag.valueOf(depth));
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag main_tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(main_tag, registries);
        unresolved_self_state = main_tag.getCompound(BLOCK_STATE_TAG);
        if(main_tag.contains(CHAIN_TARGET_TAG))
            chain_target = NbtUtils.readBlockPos(main_tag, CHAIN_TARGET_TAG).orElse(BlockPos.ZERO);
        if(main_tag.contains(DEPTH_TAG))
            depth = main_tag.getInt(DEPTH_TAG);
    }

    // Sync to the client for the sake of pickblock
    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider registries) {
        super.onDataPacket(net, pkt, registries);
    }

    public BlockState getSelfState() {
        if(self_state == null) {
            if (Minecraft.getInstance().level == null) {
                return null;
            }
            if (Minecraft.getInstance().level.isClientSide && unresolved_self_state == null) {
                // On the client, sometimes the block entity state is not synced for at least a tick.
                // This means that things being placed and removed extremely can have no known state.
                // The server will deal with it, so there's no worries really.
                return Blocks.AIR.defaultBlockState();
            }
            setSelfState(NbtUtils.readBlockState(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), unresolved_self_state));
        }
        return self_state;
    }

    public void setSelfState(BlockState self_state) {
        this.self_state = self_state;
    }
}
