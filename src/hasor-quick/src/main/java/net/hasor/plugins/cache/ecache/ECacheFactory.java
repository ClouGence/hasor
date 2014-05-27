//package net.hasor.plugins.cache.ecache;
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.commons.lang.StringUtils;
//import org.noe.platform.context.AppContext;
//import org.noe.platform.modules.icache.Cache;
//import org.noe.platform.modules.icache.CacheDefine;
//import org.noe.platform.modules.icache.CacheFactory;
///**
// * 
// * @version : 2013-8-5
// * @author 赵永春 (zyc@byshell.org)
// */
//@CacheDefine
//public class ECacheFactory implements CacheFactory {
//    public static final String        DefaultName = "DEFAULT";
//    private static Map<String, Cache> cacheMap    = new HashMap<String, Cache>();
//    @Override
//    public Cache createCache(AppContext appContext, String groupName) {
//        if (StringUtils.isBlank(groupName))
//            groupName = DefaultName;
//        //
//        Cache ecache = cacheMap.get(groupName);
//        if (ecache == null) {
//            int time = 1000000;
//            net.sf.ehcache.config.CacheConfiguration ccf = new net.sf.ehcache.config.CacheConfiguration(groupName, time);
//            ccf.setTimeToLiveSeconds(time);
//            //ccf.setTimeToIdleSeconds(time);
//            ccf.setDiskStorePath(".cache");
//            ccf.setDiskExpiryThreadIntervalSeconds(0);
//            ccf.setOverflowToDisk(false);
//            ccf.setDiskPersistent(false);
//            ccf.setEternal(false);
//            net.sf.ehcache.Cache cache = new net.sf.ehcache.Cache(ccf);
//            cache.initialise();
//            ecache = new ECache(cache);
//            cacheMap.put(groupName, ecache);
//        }
//        return ecache;
//    }
//}