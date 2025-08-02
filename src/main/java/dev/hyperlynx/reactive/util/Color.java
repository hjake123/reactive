package dev.hyperlynx.reactive.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hyperlynx.reactive.alchemy.Power;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public class Color {
    public int red;
    public int green;
    public int blue;
    public int hex;

    public static final Codec<Color> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("red").forGetter(Color::red),
            Codec.INT.fieldOf("green").forGetter(Color::green),
            Codec.INT.fieldOf("blue").forGetter(Color::blue))
            .apply(instance, Color::new)
    );

    public static final StreamCodec<ByteBuf, Color> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, Color::hex,
            Color::new
    );

    public Color(int color) {
        hex = color;
        red = (((color >> 16) & 0xFF));
        green = (((color >> 8) & 0xFF));
        blue = ((color & 0xFF));
    }

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        updateHexFromRGB();
    }

    public static Color black() {
        return new Color(0);
    }

    public static Color white() {
        return new Color(0xFFFFFF);
    }

    public int hex() { return hex; }
    private int red() { return red; }
    private int green() { return green; }
    private int blue() { return blue; }

    public void reset(){
        red = 0;
        green = 0;
        blue = 0;
        hex = 0;
    }

    public void set(Color to){
        red = to.red;
        green = to.green;
        blue = to.blue;
        hex = to.hex;
    }

    @Override
    public boolean equals(Object obj) {
        boolean obj_equals = super.equals(obj);
        if(obj instanceof Color){
            return red == ((Color) obj).red && green == ((Color) obj).green && blue == ((Color) obj).blue;
        }
        return obj_equals;
    }

    public void updateHexFromRGB() {
        hex = red << 16 | green << 8 | blue;
    }

    public void setMixColor(Color base_color, Map<Power, Integer> powers, int total_visible_power, int max_power) {
        reset();
        for (Power p : powers.keySet()) {
            if(p == null || p.invisible){
                continue; // Skip any invalid or invisible powers.
            }
            Color pow_color = p.getColor();
            float pow_weight = powers.get(p) / (float) total_visible_power;
            red += (int) (pow_color.red * pow_weight);
            green += (int) (pow_color.green * pow_weight);
            blue += (int) (pow_color.blue * pow_weight);
        }

        // Adjust the tint to be proportional to the amount of the maximum currently in use.
        float tint_alpha = (float) total_visible_power / (float) max_power;
        red = (int) (base_color.red * (1 - tint_alpha) + red * (tint_alpha));
        green = (int) (base_color.green * (1 - tint_alpha) + green * (tint_alpha));
        blue = (int) (base_color.blue * (1 - tint_alpha) + blue * (tint_alpha));
        updateHexFromRGB();
    }
}
