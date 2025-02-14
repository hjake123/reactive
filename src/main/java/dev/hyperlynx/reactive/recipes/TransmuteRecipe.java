package dev.hyperlynx.reactive.recipes;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.alchemy.Power;
import dev.hyperlynx.reactive.alchemy.PowerBearer;
import dev.hyperlynx.reactive.alchemy.Powers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransmuteRecipe implements Recipe<CrucibleRecipeInput> {
    protected final String group;
    protected final Ingredient reactant;
    protected final ItemStack product;
    protected final List<ResourceKey<Power>> reagents;
    int cost;
    int minimum;
    public boolean needs_electricity;

    public TransmuteRecipe(String group, Ingredient reactant, ItemStack product, List<ResourceKey<Power>> reagents, int min, int cost, boolean needs_electricity) {
        this.group = group;
        this.reactant = reactant;
        this.product = product;
        this.reagents = reagents;
        this.minimum = min;
        this.cost = cost;
        this.needs_electricity = needs_electricity;
    }

    public @NotNull String getGroup(){
        return group;
    }

    private List<Holder.Reference<Power>> powerHolders(RegistryAccess access) {
        return reagents.stream().map((key) -> access.lookup(Powers.POWER_REGISTRY_KEY).get().get(key).get()).toList();
    }

    private boolean powerMet(CrucibleRecipeInput input, RegistryAccess access){
        int power_level = 0;
        boolean has_all_reagents = true;
        for(Holder.Reference<Power> p : powerHolders(access)) {
            if(input.getPowerLevel(p.value()) == 0){
                has_all_reagents = false;
                break;
            }
            power_level += input.getPowerLevel(p.value());
        }
        return has_all_reagents && power_level > minimum;
    }

    public ItemStack apply(ItemStack input, PowerBearer bearer, Level level) {
        int max_tfs = Integer.MAX_VALUE;
        if(cost > 0) {
            for (Holder.Reference<Power> p : powerHolders(level.registryAccess())) {
                max_tfs = Math.min(max_tfs, (bearer.getPowerLevel(p.value()) / (cost / reagents.size())));
                bearer.expendPower(p.value(), cost / reagents.size() * input.getCount());
            }
        }
        ItemStack result = product.copy();
        result.setCount(Math.min(input.getCount(), max_tfs)*result.getCount());
        input.setCount(input.getCount() - Math.min(input.getCount(), max_tfs));
        return result;
    }

    @Override
    public boolean matches(@NotNull CrucibleRecipeInput input, @NotNull Level level) {
        for(ItemStack i : reactant.getItems()) {
            if (input.getItem().is(i.getItem())) {
                return powerMet(input, level.registryAccess());
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CrucibleRecipeInput input, HolderLookup.@NotNull Provider provider) {
        return product.copy();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return product;
    }

    public ItemStack getProduct() {
        return product;
    }

    public Ingredient getReactant(){ return reactant; }

    public List<ResourceKey<Power>> getReagentKeys(){ return reagents; }

    public List<Power> getReagents(RegistryAccess access) {
        return reagents.stream().map((key) ->
                Powers.getPowerRegistry(access).get(key)).toList();
    }

    public int getCost(){ return cost; }

    public int getMinimum(){ return minimum; }

    public boolean isElectricityRequired(){ return needs_electricity; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.TRANS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Registration.TRANS_RECIPE_TYPE.get();
    }

    // No, these recipes aren't for the recipe book, Mojang...

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
