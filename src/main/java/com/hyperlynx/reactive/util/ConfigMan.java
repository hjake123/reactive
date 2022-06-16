package com.hyperlynx.reactive.util;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigMan {
    public static class Common {
        public ForgeConfigSpec.BooleanValue parachuteQuilt;
        public ForgeConfigSpec.BooleanValue ragingBurning;
        public ForgeConfigSpec.IntValue rageRange;
        public ForgeConfigSpec.BooleanValue mourningTP;
        public ForgeConfigSpec.IntValue mourningRange;
        public ForgeConfigSpec.IntValue insomniaTicks;
        public ForgeConfigSpec.IntValue quiltActivateHeight;
        public ForgeConfigSpec.BooleanValue laserBlindness;
        public ForgeConfigSpec.IntValue laserCatRange;


        Common(ForgeConfigSpec.Builder builder){
            builder.comment("Config Settings")
                    .push("config");
            laserBlindness = builder.comment("Shining the Pointer into an entity's eyes blinds them briefly. [Default: true]")
                    .define("laserBlindness", true);
            builder.pop();
        }
    }

    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

}
