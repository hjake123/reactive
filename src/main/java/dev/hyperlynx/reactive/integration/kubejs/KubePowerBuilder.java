package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.registration.ReactiveBlocks;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBuilder;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("unused")
public class KubePowerBuilder extends BuilderBase<Power> {
    private final PowerBuilder builder;

    public KubePowerBuilder(ResourceLocation id) {
        super(id);
        this.builder = new PowerBuilder(id);
    }

    @Override
    public Power createObject() {
        return builder.build();
    }

    public KubePowerBuilder color(int color){
        builder.color(color);
        return this;
    }

    public KubePowerBuilder bottle(Item bottle){
        builder.bottle(bottle);
        return this;
    }

    public KubePowerBuilder setNormalWater(){
        builder.water(Blocks.WATER);
        return this;
    }

    public KubePowerBuilder setMagicWater(){
        builder.water(ReactiveBlocks.DUMMY_MAGIC_WATER.get());
        return this;
    }
    public KubePowerBuilder setFastWater(){
        builder.water(ReactiveBlocks.DUMMY_FAST_WATER.get());
        return this;
    }
    public KubePowerBuilder setNoiseWater(){
        builder.water(ReactiveBlocks.DUMMY_NOISE_WATER.get());
        return this;
    }
    public KubePowerBuilder setSlowWater(){
        builder.water(ReactiveBlocks.DUMMY_SLOW_WATER.get());
        return this;
    }

    public KubePowerBuilder setCustomWater(Block water){
        builder.water(water);
        return this;
    }

    public KubePowerBuilder setInvisible(){
        builder.invisible = true;
        return this;
    }

    public KubePowerBuilder setName(MutableComponent name){
        builder.setName(name);
        return this;
    }
}
