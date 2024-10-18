package dev.hyperlynx.reactive.integration.kubejs;

import dev.hyperlynx.reactive.be.CrucibleBlockEntity;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;

public class KubeCrucible implements CustomJavaToJsWrapper {
    public CrucibleBlockEntity crucible;

    public KubeCrucible(CrucibleBlockEntity crucible){
        this.crucible = crucible;
    }

    @Override
    public Scriptable convertJavaToJs(Context context, Scriptable scriptable, TypeInfo typeInfo) {
        return new NativeJavaCrucible(scriptable, crucible, typeInfo, context);
    }

    static class NativeJavaCrucible extends NativeJavaObject {
        public NativeJavaCrucible(Scriptable scope, CrucibleBlockEntity crucible, TypeInfo typeInfo, Context cx) {
            super(scope, crucible, typeInfo, cx);
        }
    }
}
