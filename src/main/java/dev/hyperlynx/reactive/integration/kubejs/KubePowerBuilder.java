package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBuilder;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class KubePowerBuilder extends BuilderBase<Power> {
    private final PowerBuilder builder;

    public KubePowerBuilder(ResourceLocation id) {
        super(id);
        this.builder = new PowerBuilder(id);
    }

    @Override
    public RegistryInfo getRegistryType() {
        return ReactiveKubeJSPlugin.POWER_REGISTRY_INFO;
    }

    @Override
    public Power createObject() {
        return builder.build();
    }

    public KubePowerBuilder color(int color){
        builder.color(color);
        return this;
    }

    public KubePowerBuilder bottle(ResourceLocation bottle_id){
        builder.bottle = () -> getItem(bottle_id, false);
        return this;
    }

    public KubePowerBuilder setNormalWater(){
        builder.water(() -> Blocks.WATER);
        return this;
    }

    public KubePowerBuilder setMagicWater(){
        builder.water(Registration.DUMMY_MAGIC_WATER);
        return this;
    }
    public KubePowerBuilder setFastWater(){
        builder.water(Registration.DUMMY_FAST_WATER);
        return this;
    }
    public KubePowerBuilder setNoiseWater(){
        builder.water(Registration.DUMMY_NOISE_WATER);
        return this;
    }

    public KubePowerBuilder setCustomWater(Block water){
        builder.water(() -> water);
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

    @SuppressWarnings("deprecation")
    public Item getItem(ResourceLocation id, boolean barrier_if_invalid){
        Item item = BuiltInRegistries.ITEM.get(id);
        if(item == Items.AIR){
            if(barrier_if_invalid){
                return Items.BARRIER;
            }
            return null;
        }
        return item;
    }
}
