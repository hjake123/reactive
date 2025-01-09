package dev.hyperlynx.reactive.items;

import dev.hyperlynx.reactive.Registration;
import dev.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// Absorbs negative Crucible effects and Potion effects.
public class CrystalIronItem extends Item {
    public CrystalIronItem(Properties props) {
        super(props);
    }

    // Return whether the given entity should be subjected to an effect (i.e. if there was no Crystal Iron blocking it.
    // Also damages the item if necessary.
    public static boolean effectNotBlocked(LivingEntity e, int cost) {
        if(e.isHolding(Registration.CRYSTAL_IRON.get())) {
            if(cost > 0) {
                if (e.getOffhandItem().is(Registration.CRYSTAL_IRON.get())) {
                    e.getOffhandItem().hurtAndBreak(cost, e, (LivingEntity l) -> {});
                } else {
                    e.getMainHandItem().hurtAndBreak(cost, e, (LivingEntity l) -> {});
                }
            }
            return false;
        }else if(e instanceof Player && ((Player) e).getInventory().hasAnyMatching((ItemStack stack) -> stack.is(Registration.CRYSTAL_IRON.get()))){
            if(cost > 0){
                for(ItemStack stack : ((Player) e).getInventory().items){
                    if(stack.is(Registration.CRYSTAL_IRON.get())){
                        stack.hurtAndBreak(cost, (Player) e, (Player s) -> {});
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int tick, boolean unknown) {
        if(entity instanceof LivingEntity bearer && !level.isClientSide){
            if(bearer.getActiveEffects().isEmpty()){
                return;
            }
            List<MobEffect> effects_to_remove = new ArrayList<>(List.of(MobEffects.WITHER, MobEffects.POISON));

            if(WorldSpecificValue.getBool("stone_break_hunger", 0.7F))
                effects_to_remove.add(MobEffects.HUNGER);

            if(WorldSpecificValue.getBool("stone_break_slow", 0.3F))
                effects_to_remove.add(MobEffects.MOVEMENT_SLOWDOWN);

            if(WorldSpecificValue.getBool("stone_break_weakness", 0.5F))
                effects_to_remove.add(MobEffects.WEAKNESS);

            for(MobEffect effect : effects_to_remove){
                if(bearer.removeEffect(effect))
                    getHurt(stack, bearer);
            }
        }
    }

    private void getHurt(ItemStack stack, LivingEntity holder){
        stack.hurtAndBreak(1, holder, (LivingEntity l) -> {});
    }
}
