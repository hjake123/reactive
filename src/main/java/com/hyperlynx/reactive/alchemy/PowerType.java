package com.hyperlynx.reactive.alchemy;

// This class represents one of the kinds of Alchemical Power that items can produce when put into the crucible. It's similar to Item.
public class PowerType {
    private final int color;
    public PowerType(int color){
        this.color = color;
    }

    public int getColor(){
        return color;
    }
}
