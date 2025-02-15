package dev.hyperlynx.reactive.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Color {
    public int red;
    public int green;
    public int blue;
    public int hex;

    public Color(int color){
        hex = color;
        red = (((color >> 16) & 0xFF));
        green = (((color >> 8) & 0xFF));
        blue = ((color & 0xFF));
    }

    public Color() {}

    public Color(int red, int green, int blue){
        if(red > 0xFF || green > 0xFF || blue > 0xFF || red < 0 || green < 0 || blue < 0) {
            throw new RuntimeException("Invalid color parameters (" + red + ", " + green + ", " + blue + "); RGB must be with [0, 255]");
        }
        this.red = red;
        this.green = green;
        this.blue = blue;
        hex = red << 16 | green << 8 | blue;
    }

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

    public int red(){
        return red;
    }
    public int green(){
        return green;
    }
    public int blue(){
        return blue;
    }
    public int hex(){
        return hex;
    }

    public static Codec<Color> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.INT.fieldOf("red").forGetter(Color::red),
                    Codec.INT.fieldOf("green").forGetter(Color::green),
                    Codec.INT.fieldOf("blue").forGetter(Color::blue)
            ).apply(instance, Color::new)
    );
}
