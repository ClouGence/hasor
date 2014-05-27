//package net.hasor.plugins.cache.ecache;
//import net.sf.ehcache.Element;
//import org.noe.platform.modules.icache.Cache;
///**
// * 
// * @version : 2013-8-5
// * @author 赵永春 (zyc@byshell.org)
// */
//public class ECache implements Cache {
//    private net.sf.ehcache.Cache cacheObject = null;
//    public ECache(net.sf.ehcache.Cache cacheObject) {
//        this.cacheObject = cacheObject;
//    }
//    //
//    @Override
//    public boolean toCache(String key, Object value, long timeout) {
//        Element element = new Element(key, value);
//        cacheObject.put(element);
//        return true;
//    }
//    @Override
//    public Object fromCache(String key) {
//        Element element = cacheObject.get(key);
//        if (element != null && element.getObjectValue() != null && element.isExpired() == false) {
//            return element.getObjectValue();
//        }
//        return null;
//    }
//    @Override
//    public boolean hasCache(String key) {
//        return fromCache(key) != null;
//    }
//    @Override
//    public boolean remove(String key) {
//        cacheObject.remove(key);
//        return true;
//    }
//    @Override
//    public boolean clear() {
//        cacheObject.removeAll();
//        return true;
//    }
//    @Override
//    public void close() {
//        cacheObject.flush();
//        cacheObject.dispose();
//    }
//}