/**********************************************************\
 |                                                          |
 |                          hprose                          |
 |                                                          |
 | Official WebSite: http://www.hprose.com/                 |
 |                   http://www.hprose.org/                 |
 |                                                          |
 \**********************************************************/
/**********************************************************\
 *                                                        *
 * ClassManager.java                                      *
 *                                                        *
 * ClassManager for Java.                                 *
 *                                                        *
 * LastModified: Apr 20, 2015                             *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
 \**********************************************************/
package net.hasor.rsf.libs.com.hprose.io;
import java.util.concurrent.ConcurrentHashMap;
public final class HproseClassManager {
    private final static ConcurrentHashMap<Class<?>, String> classCache1 = new ConcurrentHashMap<Class<?>, String>();
    private final static ConcurrentHashMap<String, Class<?>> classCache2 = new ConcurrentHashMap<String, Class<?>>();
    private HproseClassManager() {
    }
    public final static void register(Class<?> type, String alias) {
        classCache1.put(type, alias);
        classCache2.put(alias, type);
    }
    public final static String getClassAlias(Class<?> type) {
        return classCache1.get(type);
    }
    public final static Class<?> getClass(String alias) {
        return classCache2.get(alias);
    }
    public final static boolean containsClass(String alias) {
        return classCache2.containsKey(alias);
    }
}