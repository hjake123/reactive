package com.hyperlynx.reactive.util;

public class Color {
    public int red;
    public int green;
    public int blue;

    public Color(int color){
        red = (int) (((color >> 16) & 0xFF));
        green = (int) (((color >> 8) & 0xFF));
        blue = (int) ((color & 0xFF));
    }

    public Color(){

    }

}
