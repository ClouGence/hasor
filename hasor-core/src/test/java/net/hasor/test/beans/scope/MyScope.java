package net.hasor.test.beans.scope;
import net.hasor.core.Scope;
import net.hasor.core.provider.SingleProvider;

import java.util.HashMap;
import java.util.function.Supplier;
public class MyScope implements Scope {
    private HashMap<Object, Supplier<?>> scopeMap = new HashMap<>();
    public <T> Supplier<T> scope(Object key, final Supplier<T> provider) {
        Supplier<?> returnData = this.scopeMap.get(key);
        if (returnData == null) {
            Supplier<T> newSingleProvider = new SingleProvider<T>(provider);
            returnData = this.scopeMap.putIfAbsent(key, newSingleProvider);
            if (returnData == null) {
                returnData = newSingleProvider;
            }
        }
        return (Supplier<T>) returnData;
    }
}