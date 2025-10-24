package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.material.*;
import dev.hyperlynx.reactive.alchemy.material.formula.Formula;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/// For creating custom built-in Materials using KubeJS
public class MaterialFactory {
    private final BuiltInMaterials.BuiltInMaterialEvent event;
    private final ResourceLocation id;
    private String name = "";
    private final Map<MaterialProperty<?>, Object> properties = new HashMap<>();
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

    public MaterialFactory model(String model) {
        if(!(MaterialModel.isNameValid(model))) {
            ReactiveMod.LOGGER.error("KubeJS script used invalid material model.");
            ReactiveMod.LOGGER.error("Valid models include {}", Arrays.stream(MaterialModel.values()).sequential().map(MaterialModel::getSerializedName).toList());
            throw new KubeScriptException("Model '" + model + "' was invalid.");
        }
        properties.put(MaterialProperties.MODEL_NAME.get(), model);
        return this;
    }

    public MaterialFactory color(int hex) {
        if(hex < 0 || hex > 0xFFFFFF) {
            throw new KubeScriptException("Invalid RBG color! Did you mistype?");
        }
        properties.put(MaterialProperties.COLOR.get(), new Color(hex));
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

    public MaterialFactory redstone(int signal) {
        if(signal < 0 || signal > 15) {
            throw new KubeScriptException("Can't set redstone signal to a value outside of [0, 15]!");
        }
        properties.put(MaterialProperties.REDSTONE.get(), signal);
        return this;
    }

    public MaterialFactory breakStrength(float strength) {
        properties.put(MaterialProperties.BREAK_STRENGTH.get(), strength);
        return this;
    }

    public MaterialFactory blastResistance(float strength) {
        properties.put(MaterialProperties.BLAST_RESISTANCE.get(), strength);
        return this;
    }

    public MaterialFactory enchantPower(float power) {
        properties.put(MaterialProperties.ENCHANT_POWER.get(), power);
        return this;
    }

    public MaterialFactory friction(float friction) {
        properties.put(MaterialProperties.FRICTION.get(), friction);
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

    public MaterialFactory selfDefense(float damage) {
        properties.put(MaterialProperties.SELF_DEFENSE.get(), damage);
        return this;
    }

    public MaterialFactory intangible() {
        properties.put(MaterialProperties.INTANGIBLE.get(), Unit.INSTANCE);
        return this;
    }

    public MaterialFactory semitangible() {
        properties.put(MaterialProperties.SEMITANGIBLE.get(), Unit.INSTANCE);
        return this;
    }

    public MaterialFactory formulaBase(ResourceLocation item_id) {
        Item base = ForgeRegistries.ITEMS.getValue(item_id);
        if(base == null) {
            throw new KubeScriptException("Invalid item id " + item_id);
        }
        base_material = base;
        return this;
    }

    public MaterialFactory addFormulaCost(Power reagent, int cost) {
        power_costs.put(reagent, cost);
        return this;
    }


    public void build() {
        Material material;
        if(base_material != null && !power_costs.isEmpty()) {
            material = new Material(properties, name, new Formula(power_costs, base_material), null, "");
        } else {
            material = new Material(properties, name, null, null, "");
        }
        event.addMaterial(id, material);
    }
}
