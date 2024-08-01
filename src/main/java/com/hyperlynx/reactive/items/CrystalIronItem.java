package com.hyperlynx.reactive.items;

import com.hyperlynx.reactive.Registration;
import com.hyperlynx.reactive.util.WorldSpecificValue;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
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
    public static boolean effectNotBlocked(LivingEntity entity, int cost) {
        if(entity.isHolding(Registration.CRYSTAL_IRON.get())) {
            if(cost > 0) {
                if (entity.getOffhandItem().is(Registration.CRYSTAL_IRON.get())) {
                    entity.getOffhandItem().hurtAndBreak(cost, entity, entity.getOffhandItem().getEquipmentSlot());
                } else {
                    entity.getMainHandItem().hurtAndBreak(cost, entity, entity.getMainHandItem().getEquipmentSlot());
                }
            }
            return false;
        }else if(entity instanceof Player player && player.getInventory().hasAnyMatching((ItemStack stack) -> stack.is(Registration.CRYSTAL_IRON.get()))){
            if(cost > 0){
                for(ItemStack stack : player.getInventory().items){
                    if(stack.is(Registration.CRYSTAL_IRON.get())){
                        stack.hurtAndBreak(cost, player.level().random, player, () -> {});
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
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot_number, boolean unknown) {
        if(entity instanceof LivingEntity holder && !level.isClientSide){
            if(holder.getActiveEffects().isEmpty()){
                return;
            }

            EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);

            List<Holder<MobEffect>> effects_to_remove = new ArrayList<>(List.of(MobEffects.WITHER, MobEffects.POISON));

            if(WorldSpecificValue.getBool("stone_break_hunger", 0.7F))
                effects_to_remove.add(MobEffects.HUNGER);

            if(WorldSpecificValue.getBool("stone_break_slow", 0.3F))
                effects_to_remove.add(MobEffects.MOVEMENT_SLOWDOWN);

            if(WorldSpecificValue.getBool("stone_break_weakness", 0.5F))
                effects_to_remove.add(MobEffects.WEAKNESS);

            for(Holder<MobEffect> effect : effects_to_remove){
                if(holder.removeEffect(effect))
                    stack.hurtAndBreak(1, holder, slot);
            }
        }
    }
}
