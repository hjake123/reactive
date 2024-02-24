package com.hyperlynx.reactive.effect;

import com.hyperlynx.reactive.ReactiveMod;
import com.hyperlynx.reactive.Registration;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=ReactiveMod.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EffectHandlers {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event){
        if(event.getEntity().hasEffect(Registration.FIRE_SHIELD.get()) && event.getSource().getDirectEntity() != null && !event.getSource().getDirectEntity().fireImmune()){
            event.getSource().getDirectEntity().hurt(event.getEntity().damageSources().inFire(), 2);
            event.getSource().getDirectEntity().setSecondsOnFire(5);
            float damage = event.getAmount();
            if(damage < 2)
                event.setCanceled(true);
            else
                event.setAmount(damage - 2);
        }
    }
}
