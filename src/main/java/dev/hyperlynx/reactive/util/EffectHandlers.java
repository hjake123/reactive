package dev.hyperlynx.reactive.util;

import dev.hyperlynx.reactive.ReactiveMod;
import dev.hyperlynx.reactive.Registration;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid= ReactiveMod.MODID, bus=EventBusSubscriber.Bus.GAME)
public class EffectHandlers {
    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event){
        if(event.getEntity().hasEffect(Registration.FIRE_SHIELD) && event.getSource().getDirectEntity() != null && !event.getSource().getDirectEntity().fireImmune()){
            event.getSource().getDirectEntity().hurt(event.getEntity().damageSources().inFire(), 2);
            event.getSource().getDirectEntity().setRemainingFireTicks(100);
            float damage = event.getAmount();
            if(damage < 2)
                event.setCanceled(true);
            else
                event.setAmount(damage - 2);
        }
    }
    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if(event.getEntity().hasEffect(Registration.IMMOBILE)){
            event.getEntity().setJumping(false);
            event.getEntity().setDeltaMovement(0, 0, 0);
        }
    }
}
