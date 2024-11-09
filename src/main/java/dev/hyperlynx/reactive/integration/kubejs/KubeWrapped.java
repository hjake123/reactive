package dev.hyperlynx.reactive.integration.kubejs;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;

public class KubeWrapped<T> implements CustomJavaToJsWrapper {
    private final T thing;

    public KubeWrapped(T thing){
        this.thing = thing;
    }

    public T get(){
        return thing;
    }

    @Override
    public Scriptable convertJavaToJs(Context context, Scriptable scriptable, TypeInfo typeInfo) {
        return new NativeJavaObject(scriptable, thing, typeInfo, context);
    }
}
