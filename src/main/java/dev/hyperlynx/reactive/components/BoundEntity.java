package dev.hyperlynx.reactive.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record BoundEntity(String name, UUID uuid) {
    public static final Codec<BoundEntity> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.STRING.fieldOf("target").forGetter(BoundEntity::name),
                    UUIDUtil.CODEC.fieldOf("UUID").forGetter(BoundEntity::uuid)
            ).apply(instance, BoundEntity::new)
    );

    public static final StreamCodec<ByteBuf, BoundEntity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BoundEntity::name,
            UUIDUtil.STREAM_CODEC, BoundEntity::uuid,
            BoundEntity::new
    );
}
