package dev.hyperlynx.reactive.components;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionStatusEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public record LitmusMeasurement(List<Line> measurements, List<ReactionStatusEntry> statuses, boolean integrity_violated) {
    public static final Codec<LitmusMeasurement> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Line.CODEC.listOf().fieldOf("measurements").forGetter(LitmusMeasurement::measurements),
                    ReactionStatusEntry.CODEC.listOf().fieldOf("status").forGetter(LitmusMeasurement::statuses),
                    Codec.BOOL.fieldOf("integrity_violation").forGetter(LitmusMeasurement::integrity_violated)
            ).apply(instance, LitmusMeasurement::new)
    );

    public static final StreamCodec<ByteBuf, LitmusMeasurement> STREAM_CODEC = StreamCodec.composite(
            Line.STREAM_CODEC.apply(ByteBufCodecs.list()), LitmusMeasurement::measurements,
            ReactionStatusEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), LitmusMeasurement::statuses,
            ByteBufCodecs.BOOL, LitmusMeasurement::integrity_violated,
            LitmusMeasurement::new
    );

    public record Line(ResourceKey<Power> power, String line){
        public static final Codec<Line> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Power.RESOURCE_KEY_CODEC.fieldOf("power").forGetter(Line::power),
                        Codec.STRING.fieldOf("line").forGetter(Line::line)
                ).apply(instance, Line::new)
        );

        public static final StreamCodec<ByteBuf, Line> STREAM_CODEC = StreamCodec.composite(
                Power.RESOURCE_KEY_STREAM_CODEC, Line::power,
                ByteBufCodecs.STRING_UTF8, Line::line,
                Line::new
        );
    }
}
