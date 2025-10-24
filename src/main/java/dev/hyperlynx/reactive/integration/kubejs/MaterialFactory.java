package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.*;
import dev.hyperlynx.reactive.alchemy.material.formula.Formula;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/// For creating custom built-in Materials using KubeJS
public class MaterialFactory {
    private final BuiltInMaterials.BuiltInMaterialEvent event;
    private final ResourceLocation id;
    private String name = "";
    private final Map<MaterialProperty<?>, Object> properties = new HashMap<>();
    private boolean defined_formula = false;
    private final Map<Power, Integer> power_costs = new HashMap<>();
    private Item base_material;

    public MaterialFactory(BuiltInMaterials.BuiltInMaterialEvent event, ResourceLocation id) {
        this.event = event;
        this.id = id;
    }

    public MaterialFactory defaultName(String name) {
        this.name = name;
        return this;
    }

    public MaterialFactory color(int hex) {
        properties.put(MaterialProperties.COLOR.get(), hex);
        return this;
    }

    public MaterialFactory setModel(String model) {
        if(!(MaterialModel.isNameValid(model))) {
            ReactiveMod.LOGGER.error("KubeJS script used invalid material model.");
            ReactiveMod.LOGGER.error("Valid models include {}", Arrays.stream(MaterialModel.values()).sequential().map(MaterialModel::getSerializedName).toList());
            throw new KubeScriptException("Model '" + model + "' was invalid.");
        }
        properties.put(MaterialProperties.MODEL_NAME.get(), model);
        return this;
    }

    public MaterialFactory light(int light) {
        if(light < 0 || light > 15) {
            throw new KubeScriptException("Can't set light to a value outside of [0, 15]!");
        }
        properties.put(MaterialProperties.LIGHT.get(), light);
        return this;
    }

    public MaterialFactory flammability(int flammability) {
        if(flammability < 0 || flammability > 300) {
            throw new KubeScriptException("Can't set flammability to a value outside of [0, 300]!");
        }
        properties.put(MaterialProperties.FLAMMABILITY.get(), flammability);
        return this;
    }

    public MaterialFactory magmaStep() {
        properties.put(MaterialProperties.MAGMA_STEP.get(), Unit.INSTANCE);
        return this;
    }

    public MaterialFactory fireSource() {
        properties.put(MaterialProperties.FIRE_SOURCE.get(), Unit.INSTANCE);
        return this;
    }

    public MaterialFactory warping() {
        properties.put(MaterialProperties.WARPING.get(), Unit.INSTANCE);
        return this;
    }

    public MaterialFactory redstoneMelting() {
        properties.put(MaterialProperties.REDSTONE_MELTING.get(), Unit.INSTANCE);
        return this;
    }

    public MaterialFactory selfDefense() {
        properties.put(MaterialProperties.SELF_DEFENSE.get(), Unit.INSTANCE);
        return this;
    }

    // TODO more

    public void build() {
        Material material;
        if(defined_formula) {
            material = new Material(properties, name, new Formula(power_costs, base_material), null, "");
        } else {
            material = new Material(properties, name, null, null, "");
        }
        event.addMaterial(id, material);
    }
}
