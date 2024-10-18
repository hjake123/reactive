package dev.hyperlynx.reactive.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record WarpBottleTarget(@NotNull GlobalPos target) {
    public static final Codec<WarpBottleTarget> CODEC = RecordCodecBuilder.create((instance) ->
        instance.group(
            GlobalPos.CODEC.fieldOf("target").forGetter(WarpBottleTarget::target)
        ).apply(instance, WarpBottleTarget::new)
    );

    public static final StreamCodec<ByteBuf, WarpBottleTarget> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, WarpBottleTarget::target,
            WarpBottleTarget::new
    );
}
