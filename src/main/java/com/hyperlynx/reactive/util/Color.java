package com.hyperlynx.reactive.util;

public class Color {
    public int red;
    public int green;
    public int blue;

    public Color(int color){
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

}
