package com.hyperlynx.reactive.util;

import com.hyperlynx.reactive.ConfigMan;
import com.hyperlynx.reactive.ReactiveMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/*
This class represents a factory for values unique to each world, as determined by either a Server config file or the world seed.
A custom network packet is used to notify the client of the world seed.

To ensure the values are different from each other, I add the value from a hash of a placeholder alias string, which is
meant to be unique per instance. This also prevents drawing from the randomizer more than once.

When called outside a world (by the data generator) it uses a seed of 0.
*/
public class WorldSpecificValue {
    public static long alchemy_seed = 0;
    private static long getSeed(){
        if(ConfigMan.serverSpec.isLoaded() && ConfigMan.SERVER.useCustomSeed.get())
            return ConfigMan.SERVER.seed.get();
        return alchemy_seed;
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
        if(!event.getLevel().isClientSide()){
            alchemy_seed = event.getLevel().getServer().getLevel(Level.OVERWORLD).getSeed();
            ReactiveMod.REACTION_MAN.reset();
        }
    }

    public record AlchemySeedData(long seed) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<AlchemySeedData> TYPE = new CustomPacketPayload.Type<>(ReactiveMod.location("seed_sync_payload"));
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<ByteBuf, AlchemySeedData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_LONG,
                AlchemySeedData::seed,
                AlchemySeedData::new
        );
    }

    public static class AlchemySeedPayloadHandler implements IPayloadHandler<AlchemySeedData> {

        @Override
        public void handle(@NotNull AlchemySeedData data, @NotNull IPayloadContext context) {
            context.enqueueWork(() -> {
                WorldSpecificValue.alchemy_seed = data.seed();
                ReactiveMod.REACTION_MAN.reset();
            });
        }
    }

    public record AlchemySeedConfigurationTask(ServerConfigurationPacketListener listener) implements ICustomConfigurationTask {
        public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ReactiveMod.location("alchemy_seed_config_task"));

        @Override
        public void run(Consumer<CustomPacketPayload> consumer) {
            consumer.accept(new AlchemySeedData(alchemy_seed));
            this.listener().finishCurrentTask(TYPE);
        }

        @Override
        public Type type() {
            return TYPE;
        }
    }
}
