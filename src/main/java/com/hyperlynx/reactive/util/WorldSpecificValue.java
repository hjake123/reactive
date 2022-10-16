package com.hyperlynx.reactive.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/*
This class represents a factory for values unique to each world, as determined by world seed.

To ensure the values are different from each other, I add the value from a hash of a placeholder alias string, which is
meant to be unique per instance. This also prevents drawing from the randomizer more than once.
*/
public class WorldSpecificValue {

    public static int get(Level l, String alias, int min, int max){
        if(l.isClientSide()){
            return -1;
        }
        return get(l.getServer().getLevel(Level.OVERWORLD), alias, min, max);
    }

    public static int get(ServerLevel l, String alias, int min, int max){
        if(l == null){
            return -1;
        }
        long world_seed = l.getSeed();
        Random rand = new Random(world_seed + alias.hashCode());
        return rand.nextInt(max-min + 1) + min;
    }

    public static boolean getBool(ServerLevel l, String alias, float chance){
        if(l == null){
            return false;
        }
        long world_seed = l.getSeed();
        Random rand = new Random(world_seed + alias.hashCode());
        return rand.nextFloat() < chance;
    }

//    public static boolean getBool(BlockGetter b, String alias, float chance){
//        long world_seed = b.getBlockState(BlockPos.ZERO).getSeed(BlockPos.ZERO);
//        System.out.println(world_seed);
//        Random rand = new Random(world_seed + alias.hashCode());
//        return rand.nextFloat() < chance;
//    }

    public static <T> T getFromCollection(Level l, String alias, Collection<T> c) {
        int index = get(l, alias, 0, c.size()-1);
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
    public static <T> ArrayList<T> shuffle(Level l, String alias, ArrayList<T> list){
        ArrayList<T> input = new ArrayList<>(list);
        ArrayList<T> output = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            int x = get(l, alias+i, 0, input.size()-1);
            output.add(input.get(x));
            input.remove(x);
        }
        return output;
    }
}
