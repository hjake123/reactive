package com.hyperlynx.reactive;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigMan {
    public static class Common {
        public ModConfigSpec.IntValue crucibleTickDelay;
        public ModConfigSpec.IntValue crucibleRange;
        public ModConfigSpec.IntValue areaMemoryRange;
        public ModConfigSpec.IntValue maxDisplaceCount;
        public ModConfigSpec.IntValue displaceConductRange;
        public ModConfigSpec.DoubleValue maxMoveBlockBreakTime;
        public ModConfigSpec.ConfigValue<List<String>> doNotTeleport;
        public ModConfigSpec.BooleanValue acidMeltBlockEntities;
        public ModConfigSpec.BooleanValue lightStaffLightsPermanent;

        Common(ModConfigSpec.Builder builder){
            builder.comment("Options:")
                    .push("config");
            crucibleTickDelay = builder.comment("The crucible performs a stage of its calculations once every X game ticks. Lower numbers are more responsive, but laggier. [Default: 5]")
                    .defineInRange("crucibleTickDelay", 5, 1, 900);
            crucibleRange = builder.comment("The crucible affect entities with an area of this radius. [Default: 12]")
                    .defineInRange("crucibleRange", 12, 2, 64);
            areaMemoryRange = builder.comment("The crucible checks an area this many blocks in radius up to a few times a second. Do not set this too high. [Default: 6]")
                    .defineInRange("areaMemoryRange", 6, 2, 64);
            doNotTeleport = builder.comment("Certain effects might teleport entities if they are not in this blacklist. [Default: \"minecraft:ender_dragon\", \"minecraft:wither\", \"minecraft:warden\"]")
                    .define("doNotTeleport", Lists.newArrayList("minecraft:ender_dragon", "minecraft:wither", "minecraft:warden"));
            acidMeltBlockEntities = builder.comment("Whether acid should dissolve entity blocks. This would delete the contents of said blocks. [Default: false]")
                    .define("acidMeltBlockEntities", false);
            maxMoveBlockBreakTime = builder.comment("Blocks with a base break time beyond this cannot be displaced or made to fall. For finer control, use the relevant block tags. [Default: 35.0]")
                    .defineInRange("maxMoveBlockBreakTime", 35.0, 0.0, 10000.0);
            maxDisplaceCount = builder.comment("The maximum number of blocks that can be displaced at once by a certain effect. [Default: 128]")
                    .defineInRange("maxDisplaceCount", 128, 4, 4096);
            displaceConductRange = builder.comment("The maximum distance that a block like Copper can convey a displacement pulse [Default: 8]")
                    .defineInRange("copperDisplaceConductRange", 8, 1, 4096);
            lightStaffLightsPermanent = builder.comment("Whether the Radiant Staff of Power produces permanent light sources. When false, its lights will gradually vanish. [Default: true]")
                    .define("lightStaffLightsPermanent", true);
            builder.pop();
        }
    }

    public static final ModConfigSpec commonSpec;
    public static final Common COMMON;

    public static class Server {
        public ModConfigSpec.BooleanValue useWorldSeed;
        public ModConfigSpec.LongValue seed;
        public ModConfigSpec.DoubleValue pehkuiSmallSize;
        public ModConfigSpec.DoubleValue pehkuiLargeSize;


        Server(ModConfigSpec.Builder builder){
            builder.comment("World Specific Value Options:")
                    .push("wsv");
            seed = builder.comment("The seed value used to generate world-specific values. By default, it is set to your world seed on world load. If you change this, alchemy rules might change!")
                    .defineInRange("seed", 42, Long.MIN_VALUE, Long.MAX_VALUE);
            useWorldSeed = builder.comment("Whether to reset the seed to your world seed when loading.")
                    .define("resetSeed", true);
            builder.pop();
            builder.comment("Mod Integration Options:")
                    .push("integration");
            pehkuiSmallSize = builder.comment(":Requires Pehkui: The scale that the Reduction reaction sets nearby creatures to. [Default: 0.65]")
                    .defineInRange("pehkuiSmallSize", 0.65, 0.05, 0.95);
            pehkuiLargeSize = builder.comment(":Requires Pehkui: The scale that the Enlargement reaction sets nearby creatures to. [Default: 1.33]")
                    .defineInRange("pehkuiLargeSize", 1.33, 1.05, 10);
            builder.pop();
        }
    }

    public static final ModConfigSpec serverSpec;
    public static final Server SERVER;

    public static class Client {
        public ModConfigSpec.BooleanValue showPowerSources;
        public ModConfigSpec.BooleanValue doNotChangeWaterTexture;
        public ModConfigSpec.BooleanValue colorizeLitmusOutput;

        Client(ModConfigSpec.Builder builder){
            builder.comment("Client Side Options:")
                    .push("config");
            showPowerSources = builder.comment("Whether to show the sources of each Power in JEI. Use this if your pack adds a lot of unintuitive Power sources, or you become frustrated.")
                    .define("showPowerSources", false);
            doNotChangeWaterTexture = builder.comment("Whether to render all Powers using vanilla Water's icon. Use if Rubidium or other rendering mods make the custom water textures break.")
                    .define("doNotChangeWaterTexture", false);
            colorizeLitmusOutput = builder.comment("Whether to allow Litmus Paper to use multicolored text. Disable if the colored text is hard to read.")
                    .define("colorizeLitmusOutput", true);

            builder.pop();
        }
    }

    public static final ModConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Common, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(Common::new);
        commonSpec = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();

        final Pair<Server, ModConfigSpec> serverSpecPair = new ModConfigSpec.Builder().configure(Server::new);
        serverSpec = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();

        final Pair<Client, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }

}
