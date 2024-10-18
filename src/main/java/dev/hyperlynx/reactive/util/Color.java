package dev.hyperlynx.reactive.util;

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

    public Color(){

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
