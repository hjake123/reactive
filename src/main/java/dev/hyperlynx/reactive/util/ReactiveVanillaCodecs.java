package dev.hyperlynx.reactive.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactiveVanillaCodecs {
    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> {
                buffer.writeDouble(value.x);
                buffer.writeDouble(value.y);
                buffer.writeDouble(value.z);
            },
            (buffer) -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())
    );

    public static <T, U> Map<T, U> makeMapMutable(Map<T, U> map) {
        return new HashMap<>(map);
    }

    public static <T> List<T> makeListMutable(List<T> list) {
        return new ArrayList<>(list);
    }

    public static <X> X doNothing(X input) {
        return input;
    }
}
