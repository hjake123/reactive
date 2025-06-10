package dev.hyperlynx.reactive.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Color {
    public int red;
    public int green;
    public int blue;
    public int hex;

    public static final Codec<Color> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("color").forGetter(Color::hex))
            .apply(instance, Color::new)
    );

    public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Color::hex,
            Color::new
    );

    public Color(int color){
        hex = color;
        red = (((color >> 16) & 0xFF));
        green = (((color >> 8) & 0xFF));
        blue = ((color & 0xFF));
    }

    public static Color BLACK = new Color(0);
    public static Color WHITE = new Color(0xFFFFFF);

    public int hex(){
        return hex;
    }

    public void reset(){
        red = 0;
        green = 0;
        blue = 0;
    }

    public void set(Color to){
        red = to.red;
        green = to.green;
        blue = to.blue;
    }

    @Override
    public boolean equals(Object obj) {
        boolean obj_equals = super.equals(obj);
        if(obj instanceof Color){
            return red == ((Color) obj).red && green == ((Color) obj).green && blue == ((Color) obj).blue;
        }
        return obj_equals;
    }
}
