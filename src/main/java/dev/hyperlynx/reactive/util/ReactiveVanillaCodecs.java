package dev.hyperlynx.reactive.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class ReactiveVanillaCodecs {
    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> {
                buffer.writeDouble(value.x);
                buffer.writeDouble(value.y);
                buffer.writeDouble(value.z);
            },
            (buffer) -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
    );
}
