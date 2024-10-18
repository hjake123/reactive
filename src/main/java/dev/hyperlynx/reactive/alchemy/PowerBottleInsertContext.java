package dev.hyperlynx.reactive.alchemy;

import dev.hyperlynx.reactive.Registration;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import java.util.Objects;
import java.util.function.Consumer;

/*
Used by the CrucibleBlockEntity and a few other locations to work with power bottle filling in a consistent way.
 */
public class PowerBottleInsertContext {
    private final ItemStack bottle;
    private final ItemEntity entity;
    private final UseOnContext use_on_context;
    private final Consumer<PowerBottleInsertContext> reduceByOne;

    public PowerBottleInsertContext(ItemEntity entity){
        this.bottle = entity.getItem();
        this.entity = entity;
        this.use_on_context = null;
        this.reduceByOne = PowerBottleInsertContext::reduceByOneFromEntity;
    }

    public PowerBottleInsertContext(UseOnContext use_on_context){
        this.bottle = use_on_context.getItemInHand();
        this.entity = null;
        this.use_on_context = use_on_context;
        this.reduceByOne = PowerBottleInsertContext::reduceByOneFromUseOnContext;
    }

    public ItemStack getBottle(){
        return bottle;
    }

    public void reduceByOne(){
        this.reduceByOne.accept(this);
    }

    private static void reduceByOneFromEntity(PowerBottleInsertContext context){
        ItemEntity e = context.entity;
        if (e.getItem().getCount() == 1) {
            e.setItem(Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
        }
        else {
            e.getItem().shrink(1);
            ItemEntity empty_bottle = new ItemEntity(e.level(), e.getX(), e.getY(), e.getZ(), Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
            e.level().addFreshEntity(empty_bottle);
        }
    }

    private static void reduceByOneFromUseOnContext(PowerBottleInsertContext context) {
        if (context.use_on_context.getItemInHand().getCount() == 1) {
            Objects.requireNonNull(context.use_on_context.getPlayer()).setItemInHand(context.use_on_context.getHand(), Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
        }
        else {
            Objects.requireNonNull(context.use_on_context.getPlayer()).getItemInHand(context.use_on_context.getHand()).shrink(1);
            context.use_on_context.getPlayer().addItem(Registration.QUARTZ_BOTTLE.get().getDefaultInstance());
        }
    }

}
