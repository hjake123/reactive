package com.hyperlynx.reactive.util;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ConfigMan {
    public static class Common {
        public ForgeConfigSpec.IntValue crucibleTickDelay;
        public ForgeConfigSpec.IntValue crucibleRange;
        public ForgeConfigSpec.ConfigValue<List<String>> doNotTeleport;
        public ForgeConfigSpec.BooleanValue acidMeltBlockEntities;


        Common(ForgeConfigSpec.Builder builder){
            builder.comment("Options:")
                    .push("config");
            crucibleTickDelay = builder.comment("The crucible performs a stage of its calculations once every X game ticks. Lower numbers are more responsive, but laggier. [Default: 5]")
                    .defineInRange("crucibleTickDelay", 5, 1, 900);
            crucibleRange = builder.comment("The crucible may check an area this many blocks in radius for some effects. Do not set this too high. [Default: 12]")
                    .defineInRange("crucibleRange", 12, 2, 64);
            doNotTeleport = builder.comment("Certain effects might teleport entities if they are not in this blacklist. [Default: \"minecraft:ender_dragon\", \"minecraft:wither\"]")
                    .define("doNotTeleport", Lists.newArrayList("minecraft:ender_dragon", "minecraft:wither"));
            acidMeltBlockEntities = builder.comment("Whether acid should dissolve entity blocks. This would delete the contents of said blocks. [Default: false]")
                    .define("acidMeltBlockEntities", false);
            builder.pop();
        }
    }

    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    public static class Server {
        public ForgeConfigSpec.BooleanValue useWorldSeed;
        public ForgeConfigSpec.LongValue seed;

        Server(ForgeConfigSpec.Builder builder){
            builder.comment("World Specific Value Options:")
                    .push("config");
            seed = builder.comment("The seed value used to generate world-specific values. By default, it is set to your world seed on world load. If you change this, alchemy rules might change!")
                    .defineInRange("seed", 42, Long.MIN_VALUE, Long.MAX_VALUE);
            useWorldSeed = builder.comment("Whether to reset the seed to your world seed when loading.")
                    .define("resetSeed", true);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();

        final Pair<Server, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();
    }

}
