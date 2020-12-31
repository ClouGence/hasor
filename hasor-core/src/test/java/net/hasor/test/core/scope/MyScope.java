package net.hasor.test.core.scope;
import net.hasor.core.Provider;
import net.hasor.core.Scope;

import java.util.HashMap;
import java.util.function.Supplier;

public class MyScope implements Scope {
    private HashMap<Object, Supplier<?>> scopeMap = new HashMap<>();

    public <T> Supplier<T> scope(Object key, final Supplier<T> provider) {
        Supplier<?> returnData = this.scopeMap.get(key);
        if (returnData == null) {
            Supplier<T> newSingleProvider = Provider.of(provider).asSingle();
            returnData = this.scopeMap.putIfAbsent(key, newSingleProvider);
            if (returnData == null) {
                returnData = newSingleProvider;
            }
        }
        return (Supplier<T>) returnData;
    }
}