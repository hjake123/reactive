package com.hyperlynx.reactive.util;

import com.hyperlynx.reactive.ConfigMan;
import com.hyperlynx.reactive.ReactiveMod;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/*
This class represents a factory for values unique to each world, as determined by the seed in the config file.
Also, the class sets the seed to the world seed for default configurations.

To ensure the values are different from each other, I add the value from a hash of a placeholder alias string, which is
meant to be unique per instance. This also prevents drawing from the randomizer more than once.

When called outside a world (by the data generator) it uses a seed of 0.
*/
public class WorldSpecificValue {
    private static long getSeed(){
        if(ConfigMan.serverSpec.isLoaded())
            return ConfigMan.SERVER.seed.get();
        return 0;
    }
    public static Random getSource(String alias){
        long seed = getSeed();
        return new Random(seed + alias.hashCode());
    }
    public static int get(String alias, int min, int max){
        return getSource(alias).nextInt(max-min + 1) + min; // Note that this function return between min and max INCLUSIVE
    }

    public static float get(String alias, float min, float max){
        return getSource(alias).nextFloat(min, max);
    }

    public static boolean getBool(String alias, float chance){
        return getSource(alias).nextFloat() < chance;
    }

    public static <T> T getFromCollection(String alias, Collection<T> c) {
        int index = get(alias, 0, c.size()-1);
        int p = 0;
        for(T item : c){
            if(p == index){
                return item;
            }
            p++;
        }
        System.err.println("Impossibly tried to pick too high an index @ hyperlynx.reactive.util.WorldSpecificValue");
        return null;
    }

    // 'Randomize' the order of a list.
    public static <T> ArrayList<T> shuffle(String alias, List<T> list){
        ArrayList<T> input = new ArrayList<>(list);
        ArrayList<T> output = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            int x = get(alias+i, 0, input.size()-1);
            output.add(input.get(x));
            input.remove(x);
        }
        return output;
    }

    // Sets the seed in the config to your world seed if that option is selected.
    public static void worldLoad(LevelEvent.Load event){
        if(event.getLevel().isClientSide())
            return;
        long world_seed = event.getLevel().getServer().getLevel(Level.OVERWORLD).getSeed();
        if(ConfigMan.SERVER.useWorldSeed.get()){
            ConfigMan.SERVER.seed.set(world_seed);
            ConfigMan.SERVER.useWorldSeed.set(false);
        }
        ReactiveMod.REACTION_MAN.reset();
    }
}
