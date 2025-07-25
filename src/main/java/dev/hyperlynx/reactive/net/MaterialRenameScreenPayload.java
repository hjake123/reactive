package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MaterialRenameScreenPayload(ResourceLocation material_id) implements CustomPacketPayload {
    public static final Type<MaterialRenameScreenPayload> TYPE = new Type<>(ReactiveMod.location("material_rename_screen_payload"));
    public static final StreamCodec<? super FriendlyByteBuf, MaterialRenameScreenPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MaterialRenameScreenPayload::material_id,
            MaterialRenameScreenPayload::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
