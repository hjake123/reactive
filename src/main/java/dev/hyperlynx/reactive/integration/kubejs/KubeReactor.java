package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.alchemy.rxn.Reactor;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;

public class KubeReactor implements CustomJavaToJsWrapper {
    public Reactor reactor;

    public KubeReactor(Reactor reactor){
        this.reactor = reactor;
    }

    @Override
    public Scriptable convertJavaToJs(Context context, Scriptable scriptable, Class<?> aClass) {
        return new NativeJavaReactor(scriptable, reactor, aClass, context);
    }

    static class NativeJavaReactor extends NativeJavaObject {
        public NativeJavaReactor(Scriptable scope, Reactor crucible, Class<?> aClass, Context cx) {
            super(scope, crucible, aClass, cx);
        }
    }
}
