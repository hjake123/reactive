package com.hyperlynx.reactive.util;

// Just a wrapper for two integers.
public class Range {
    int min;
    int max;

    public Range(int min, int max){
        this.min = min;
        this.max = max;
    }

    public boolean inRange(int t){
        return min <= t && t <= max;
    }

    public boolean inRangeExclusive(int t){
        return min < t && t < max;
    }

    @Override
    public String toString(){
        return "(" + min + ", " + max + ")";
    }

}
