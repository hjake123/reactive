package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.server.level.ServerPlayer;
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
        if(entity instanceof LivingEntity holder && !level.isClientSide){
            if(holder.getActiveEffects().isEmpty()){
                return;
            }
            for(MobEffectInstance mei : holder.getActiveEffects()) {
                if(mei.getEffect().equals(MobEffects.WITHER)){
                    holder.removeEffect(mei.getEffect());
                    getHurt(stack, holder);
                }else if(mei.getEffect().equals(MobEffects.POISON)){
                    holder.removeEffect(mei.getEffect());
                    getHurt(stack, holder);
                }else if(mei.getEffect().equals(MobEffects.HUNGER) && WorldSpecificValue.getBool("stone_break_hunger", 0.7F)){
                    holder.removeEffect(mei.getEffect());
                    getHurt(stack, holder);
                }else if(mei.getEffect().equals(MobEffects.BAD_OMEN)){
                    holder.removeEffect(mei.getEffect());
                    getHurt(stack, holder);
                }else if(mei.getEffect().equals(MobEffects.MOVEMENT_SLOWDOWN) && WorldSpecificValue.getBool("stone_break_slow", 0.3F)){
                    holder.removeEffect(mei.getEffect());
                    getHurt(stack, holder);
                }else if(mei.getEffect().equals(MobEffects.WEAKNESS) && WorldSpecificValue.getBool("stone_break_weakness", 0.5F)){
                    holder.removeEffect(mei.getEffect());
                    getHurt(stack, holder);
                }
            }
        }
    }

    private void getHurt(ItemStack stack, LivingEntity holder){
        stack.hurtAndBreak(1, holder, (LivingEntity l) -> {});
    }
}
