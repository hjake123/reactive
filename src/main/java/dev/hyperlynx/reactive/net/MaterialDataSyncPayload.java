package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import dev.hyperlynx.reactive.alchemy.material.MaterialData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MaterialDataSyncPayload(MaterialData data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaterialDataSyncPayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("material_data_sync"));

    @Override
    public @NotNull Type<MaterialDataSyncPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialDataSyncPayload> STREAM_CODEC = StreamCodec.composite(
        MaterialData.STREAM_CODEC, MaterialDataSyncPayload::data,
        MaterialDataSyncPayload::new
    );

    public static void handle(MaterialDataSyncPayload payload, IPayloadContext ignored) {
        ClientMaterialMan.receiveDataAsync(payload.data());
    }
}
