package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.alchemy.WorldSpecificValues;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

// Absorbs negative Crucible effects and Potion effects.
public class CrystalIronItem extends Item {
    public CrystalIronItem(Properties props) {
        super(props);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int tick, boolean unknown) {
        if(entity instanceof LivingEntity holder && !level.isClientSide){
            for(MobEffectInstance mei : holder.getActiveEffects()) {
                if(mei.getEffect().equals(MobEffects.WITHER)){
                    holder.removeEffect(mei.getEffect());
                    tryToGetHurt(stack, level, holder, 2);
                }else if(mei.getEffect().equals(MobEffects.POISON)){
                    holder.removeEffect(mei.getEffect());
                    tryToGetHurt(stack, level, holder, 2);
                }else if(mei.getEffect().equals(MobEffects.HUNGER) && WorldSpecificValue.getBool(level, "stone_break_hunger", 0.7F)){
                    holder.removeEffect(mei.getEffect());
                    tryToGetHurt(stack, level, holder, 3);
                }else if(mei.getEffect().equals(MobEffects.BAD_OMEN)){
                    holder.removeEffect(mei.getEffect());
                    tryToGetHurt(stack, level, holder, 3);
                }else if(mei.getEffect().equals(MobEffects.MOVEMENT_SLOWDOWN) && WorldSpecificValue.getBool(level, "stone_break_slow", 0.3F)){
                    holder.removeEffect(mei.getEffect());
                    tryToGetHurt(stack, level, holder, 3);
                }else if(mei.getEffect().equals(MobEffects.WEAKNESS) && WorldSpecificValue.getBool(level, "stone_break_weakness", 0.5F)){
                    holder.removeEffect(mei.getEffect());
                    tryToGetHurt(stack, level, holder, 3);
                }
            }
        }
    }

    private void tryToGetHurt(ItemStack stack, Level level, LivingEntity holder, int category){
        if(WorldSpecificValues.CRYSTAL_IRON_UTILITY.get(level) != category){
            stack.hurtAndBreak(1, holder, (LivingEntity l) -> {});
        }
    }
}
