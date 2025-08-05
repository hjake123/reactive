package dev.hyperlynx.reactive.net;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.material.MaterialMan;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MaterialRenamePayload(ResourceLocation material_id, String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MaterialRenamePayload> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("material_rename"));
    public static final StreamCodec<? super FriendlyByteBuf, MaterialRenamePayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MaterialRenamePayload::material_id,
            ByteBufCodecs.STRING_UTF8, MaterialRenamePayload::name,
            MaterialRenamePayload::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        if(!(context.player().level() instanceof ServerLevel slevel)) {
            return;
        }
        MaterialMan.rename(slevel, context.player(), material_id, name);
    }
}
