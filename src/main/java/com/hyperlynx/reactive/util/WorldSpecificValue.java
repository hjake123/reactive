package com.hyperlynx.reactive.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

/*
This class represents a value unique to each world, as determined by world seed.

To ensure the values are different from each other, I add the value from a hash of a placeholder alias string, which is
meant to be unique per instance. This also prevents drawing from the randomizer more than once.
*/
public class WorldSpecificValue {

    public static int get(Level l, String alias, int max, int min){
        if(l.isClientSide()){
            return -1;
        }
        return get(l.getServer().getLevel(Level.OVERWORLD), alias, max, min);
    }

    public static int get(ServerLevel l, String alias, int max, int min){
        if(l == null){
            return -1;
        }
        long world_seed = l.getSeed();
        Random rand = new Random(world_seed + alias.hashCode());
        return rand.nextInt(max) + min;
    }

}
