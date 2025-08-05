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

public record MaterialNotesPayload(ResourceLocation material_id, String notes) implements CustomPacketPayload {
    public static final Type<MaterialNotesPayload> TYPE = new Type<>(ReactiveMod.location("material_update_notes"));
    public static final StreamCodec<? super FriendlyByteBuf, MaterialNotesPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MaterialNotesPayload::material_id,
            ByteBufCodecs.STRING_UTF8, MaterialNotesPayload::notes,
            MaterialNotesPayload::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        if(!(context.player().level() instanceof ServerLevel slevel)) {
            return;
        }
        MaterialMan.fetch(slevel, material_id).setNotes(notes);
        MaterialMan.data(slevel).setDirty();
    }
}
