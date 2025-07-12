package dev.hyperlynx.reactive.client.renderers.rxn;

import dev.hyperlynx.reactive.client.particles.EnergyParticle;
import dev.hyperlynx.reactive.registration.ReactiveParticles;
import dev.hyperlynx.reactive.alchemy.Powers;
import dev.hyperlynx.reactive.alchemy.rxn.ReactionEffects;
import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.hyperlynx.reactive.client.particles.ParticleScribe;
import dev.hyperlynx.reactive.integration.kubejs.ReactiveKubeJSPlugin;
import dev.hyperlynx.reactive.util.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;

import java.util.*;

// This class manages the renderers for reactions.
// This is important, since reaction rendering is no longer even known on the server side!
public class ReactionRenderers {
    public Map<String, ReactionRenderer> RENDERERS = new HashMap<>();

    public ReactionRenderers(){
        RENDERERS.put("curse_assimilation", this::curseRing);
        RENDERERS.put("discharge_annihilation", this::annihilationSmoke);
        RENDERERS.put("smoke_annihilation", this::smoke);
        RENDERERS.put("growth", this::growth);
        RENDERERS.put("flames", this::flamethrower);
        RENDERERS.put("size_shrink_effect", this::acid_based);
        RENDERERS.put("size_grow_effect", this::verdant_based);
        RENDERERS.put("ominous_transformation", this::ominous);
        RENDERERS.put("astral_curse_annihilation", this::creation);
        RENDERERS.put("cryo", this::snow);
        RENDERERS.put("nodule", this::warpEnergy);
    }

    public Iterable<ReactionRenderer> getRenderers(Iterable<String> aliases){
        if(ModList.get().isLoaded("kubejs")){
            ReactiveKubeJSPlugin.REACTION_EFFECT_CACHE.requestRenderers();
        }

        List<ReactionRenderer> ret = new ArrayList<>();
        for(String alias : aliases){
            if(RENDERERS.containsKey(alias)){
                ret.add(RENDERERS.get(alias));
            }
        }
        return ret;
    }

    public void smoke(Reactor reactor) {
        ParticleScribe.drawParticleReactionSurface(reactor.getLevel(), ParticleTypes.LARGE_SMOKE, reactor, 0.3F);
    }

    public void annihilationSmoke(Reactor reactor) {
        ParticleScribe.drawParticleReactionSurface(reactor.getLevel(), ParticleTypes.SMOKE, reactor, 0.2F);
    }

    public void curseRing(Reactor reactor) {
        ParticleScribe.drawExactParticleRing(reactor.getLevel(), ParticleTypes.ASH, reactor.getPos(), 0.7, 1);
    }

    public void growth(Reactor reactor) {
        ParticleScribe.drawParticleReactionSurface(reactor.getLevel(), ParticleTypes.HAPPY_VILLAGER, reactor, 0.1F);
    }

    // Shoot flames from the crucible!
    public void flamethrower(Reactor reactor) {
        if(reactor.getLevel() == null) return;

        if(reactor.getPowerLevel(Powers.SOUL_POWER.get()) > 20){
            ParticleScribe.drawParticleReactionSurface(reactor.getLevel(), ParticleTypes.SOUL_FIRE_FLAME, reactor, 0.1F, 0, 0.1, 0);
        }else{
            ParticleScribe.drawParticleReactionSurface(reactor.getLevel(), ParticleTypes.FLAME, reactor, 0.1F, 0, 0.1, 0);
        }
    }

    public void creation(Reactor reactor){
        Set<BlockPos> points = ReactionEffects.getCreationPoints(reactor.getBlockPos());
        for(BlockPos pos : points){
            if(reactor.getLevel().getBlockState(pos).isAir())
                ParticleScribe.drawParticleSphere(Objects.requireNonNull(reactor.getLevel()), ReactiveParticles.STARDUST, pos, 0.5, 1.0, 1);
        }
    }

    public void ominous(Reactor reactor) {
        ParticleScribe.drawParticleReactionSurface(reactor.getLevel(), ParticleTypes.OMINOUS_SPAWNING, reactor, 0.005F);
    }

    public void acid_based(Reactor reactor) {
        Level level = reactor.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleReactionSurface(reactor.getLevel(), ReactiveParticles.ACID_BUBBLE.getType(), reactor);
    }

    public void verdant_based(Reactor reactor) {
        Level level = reactor.getLevel();
        if(level.random.nextFloat() < 0.1F)
            ParticleScribe.drawParticleReactionSurface(level, ParticleTypes.HAPPY_VILLAGER, reactor);
    }

    public void astral(Reactor reactor) {
        if(reactor.getPowerLevel(Powers.ASTRAL_POWER.get()) < reactor.getTotalPowerLevel())
            ParticleScribe.drawExactParticleRing(reactor.getLevel(), ReactiveParticles.STARDUST.getType(), reactor.getPos(), 0.7, 1);
    }

    public void snow(Reactor reactor) {
        AABB aoe = new AABB(reactor.getBlockPos());
        aoe = aoe.inflate(5);
        ParticleScribe.drawParticleBox(reactor.getLevel(), ParticleTypes.SNOWFLAKE, aoe, 1);
    }

    public void warpEnergy(Reactor reactor) {
        if(reactor.getLevel().random.nextFloat() < 0.2F) {
            ParticleScribe.drawParticleBox(reactor.getLevel(), new EnergyParticle.Options(0.1F, Powers.WARP_POWER.get().getColor(), reactor.getPos(), true), new AABB(reactor.getBlockPos()), 1);
        }
    }
}
