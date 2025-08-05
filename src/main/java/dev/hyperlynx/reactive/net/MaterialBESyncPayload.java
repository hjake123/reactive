package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.ClientMaterialMan;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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

    public void handle(IPayloadContext context) {
        ClientMaterialMan.handleMaterialBESync(context, material_id, pos);
    }
}
