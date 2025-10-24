package dev.hyperlynx.reactive.integration.kubejs.events;

import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.material.BuiltInMaterials;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionMan;
import dev.hyperlynx.reactive.integration.kubejs.KubeScriptException;
import dev.hyperlynx.reactive.integration.kubejs.MaterialFactory;
import dev.hyperlynx.reactive.integration.kubejs.ReactionFactory;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.stream.Stream;

public class BuiltInMaterialEventJS extends EventJS {
    BuiltInMaterials.BuiltInMaterialEvent event;

    public BuiltInMaterialEventJS(BuiltInMaterials.BuiltInMaterialEvent event){
        this.event = event;
    }

    public MaterialFactory builder(ResourceLocation id){
        return new MaterialFactory(event, id);
    }

    private Power getPower(ResourceLocation location){
        Power power = Powers.POWER_SUPPLIER.get().getValue(location);
        if(power == null) {
            throw new KubeScriptException("Power " + location + " does not exist!");
        }
        return power;
    }
}
