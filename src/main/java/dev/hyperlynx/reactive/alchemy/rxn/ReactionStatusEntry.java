package dev.hyperlynx.reactive.alchemy.rxn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ReactionStatusEntry(Reaction.Status status, String reaction_alias) {
    public static ReactionStatusEntry of(String status, String reaction_alias){
        return new ReactionStatusEntry(Reaction.Status.valueOf(status), reaction_alias);
    }

    public static final Codec<ReactionStatusEntry> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.STRING.fieldOf("status").forGetter(ReactionStatusEntry::getStatusAsString),
                    Codec.STRING.fieldOf("reaction_alias").forGetter(ReactionStatusEntry::reaction_alias)
            ).apply(instance, ReactionStatusEntry::of)
    );

    public static final StreamCodec<ByteBuf, ReactionStatusEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ReactionStatusEntry::getStatusAsString,
            ByteBufCodecs.STRING_UTF8, ReactionStatusEntry::reaction_alias,
            ReactionStatusEntry::of
    );

    public String getStatusAsString(){
        return status.toString();
    }

    public Component getName() {
        return Component.translatable("reaction.reactive." + reaction_alias);
    }

    public static ReactionStatusEntry stable(){
        return new ReactionStatusEntry(Reaction.Status.STABLE, "");
    }
}
