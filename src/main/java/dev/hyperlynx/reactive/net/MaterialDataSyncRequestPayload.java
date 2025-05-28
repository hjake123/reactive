package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MaterialDataSyncRequestPayload(int index) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<MaterialDataSyncRequestPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("material_sync_request"));

    public static final StreamCodec<ByteBuf, MaterialDataSyncRequestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MaterialDataSyncRequestPayload::index,
            MaterialDataSyncRequestPayload::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<MaterialDataSyncRequestPayload> type() {
        return TYPE;
    }

    public static void handle(MaterialDataSyncRequestPayload payload, IPayloadContext context) {
        if(payload.index == -1) {
            // Send all information.
            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new MaterialDataSyncPayload(MaterialMan.data(context.player().level())));
        } else {
            throw new RuntimeException("Patch payloads are not yet implemented.");
        }
    }
}
