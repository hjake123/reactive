package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.be.MaterialBlockEntity;
import dev.hyperlynx.reactive.blocks.MaterialBlock;
import dev.hyperlynx.reactive.registration.ReactiveBlocks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MaterialBESyncPayload(ResourceLocation material_id, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaterialBESyncPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("material_be_sync"));
    public static final StreamCodec<? super FriendlyByteBuf, MaterialBESyncPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MaterialBESyncPayload::material_id,
            BlockPos.STREAM_CODEC, MaterialBESyncPayload::pos,
            MaterialBESyncPayload::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public @NotNull ClientboundCustomPayloadPacket toVanillaClientbound() {
        return CustomPacketPayload.super.toVanillaClientbound();
    }

    public void handle(IPayloadContext context) {
        if(!(context.player().level() instanceof ClientLevel clevel)) {
            return;
        }
        if(!(clevel.getBlockState(pos).getBlock() instanceof MaterialBlock)) {
            clevel.setBlock(pos, ReactiveBlocks.MATERIAL_BLOCK.get().defaultBlockState(), Block.UPDATE_NONE);
        }
        if(clevel.getBlockEntity(pos) instanceof MaterialBlockEntity mbe) {
            mbe.setMaterial(clevel, material_id);
        }
    }
}
