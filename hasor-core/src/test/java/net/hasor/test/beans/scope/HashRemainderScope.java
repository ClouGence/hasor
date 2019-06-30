package net.hasor.test.beans.scope;
import net.hasor.core.Scope;
import net.hasor.core.provider.SingleProvider;

import java.util.HashMap;
import java.util.function.Supplier;
public class HashRemainderScope implements Scope {
    private int                          modulus   = 0;
    private int                          remainder = 0;
    private HashMap<Object, Supplier<?>> scopeMap  = new HashMap<>();
    public HashRemainderScope(int modulus, int remainder) {
        this.modulus = modulus;
        this.remainder = remainder;
    }
    @Override
    public String toString() {
        return "HashRemainderScope{" + "modulus=" + modulus + ", remainder=" + remainder + '}';
    }
    public <T> Supplier<T> scope(Object key, final Supplier<T> provider) {
        Supplier<?> returnData = this.scopeMap.get(key);
        if (returnData == null) {
            T t = provider.get();
            if (t == null) {
                return provider;
            }
            //
            if (t.hashCode() % modulus == remainder) {
                Supplier<T> newSingleProvider = new SingleProvider<>(provider);
                returnData = this.scopeMap.putIfAbsent(key, newSingleProvider);
                if (returnData == null) {
                    returnData = newSingleProvider;
                }
            } else {
                returnData = provider;
            }
            //
        }
        return (Supplier<T>) returnData;
    }
    //
    public int getModulus() {
        return modulus;
    }
    public int getRemainder() {
        return remainder;
    }
    public HashMap<Object, Supplier<?>> getScopeMap() {
        return scopeMap;
    }
}