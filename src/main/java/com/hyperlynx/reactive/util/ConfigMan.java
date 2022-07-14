package com.hyperlynx.reactive.util;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigMan {
    public static class Common {
        public ForgeConfigSpec.IntValue crucibleTickDelay;

        Common(ForgeConfigSpec.Builder builder){
            builder.comment("Config Settings")
                    .push("config");
            crucibleTickDelay = builder.comment("The crucible performs its calculations once every X game ticks. Lower numbers are more responsive, but laggier. [Default: 30]")
                    .defineInRange("crucibleTickDelay", 30, 1, 900);
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
