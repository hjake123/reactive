//package dev.hyperlynx.reactive.integration.kubejs.events;
//
//import dev.hyperlynx.reactive.ReactiveMod;
//import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
//import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
//import dev.hyperlynx.reactive.integration.kubejs.CustomReaction;
//import dev.hyperlynx.reactive.integration.kubejs.KubeScriptException;
//import dev.hyperlynx.reactive.integration.kubejs.KubeWrapped;
//import dev.latvian.mods.kubejs.event.KubeEvent;
//
//public class CustomReactionTickEvent implements KubeEvent, ReactorKubeEvent {
//    KubeWrapped<Reactor> reactor;
//    CustomReaction rxn;
//
//    public CustomReactionTickEvent(CustomReaction rxn, Reactor reactor){
//        this.reactor = new KubeWrapped<>(reactor);
//        this.rxn = rxn;
//    }
//
//    @Override
//    public KubeWrapped<Reactor> getReactor() {
//        return reactor;
//    }
//
//    public String getAlias(){
//        return rxn.getAlias();
//    }
//
//    public KubeWrapped<CrucibleBlockEntity> getCrucible(){
//        if(!(reactor.get() instanceof CrucibleBlockEntity crucible)){
//            throw new KubeScriptException("Tried to get the crucible of a non-crucible reactor!");
//        }
//        return new KubeWrapped<>(crucible);
//    }
//}
