/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hasor.icache.mapcache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.hasor.context.AppContext;
import org.hasor.icache.Cache;
import org.hasor.icache.CacheDefine;
import org.hasor.icache.DefaultCache;
/**
 * 使用Map作为缓存，MapCache缓存仅作为内置提供的一个默认实现。
 * @version : 2013-4-20
 * @author 赵永春 (zyc@byshell.org)
 */
@DefaultCache
@CacheDefine(value = "MapCache", displayName = "InternalMapCache", description = "内置的Map缓存，ICache接口的简单实现。")
public class MapCache<T> extends Thread implements Cache<T> {
    private MapCacheSettings                         settings        = null;
    private String                                   threadName      = "InternalMapCache-Daemon";
    private volatile boolean                         exitThread      = false;
    private volatile HashMap<String, CacheEntity<T>> cacheEntityMap  = new HashMap<String, CacheEntity<T>>();
    private ReadWriteLock                            cacheEntityLock = new ReentrantReadWriteLock();
    //
    protected String getThreadName() {
        return this.threadName;
    }
    @Override
    public void run() {
        this.setName(this.threadName);
        while (!this.exitThread) {
            this.cacheEntityLock.writeLock().lock();//加锁(写)
            List<String> lostList = new ArrayList<String>();
            /*标记失效的元素*/
            for (Entry<String, CacheEntity<T>> ent : this.cacheEntityMap.entrySet()) {
                CacheEntity<T> cacheEnt = ent.getValue();
                if (cacheEnt == null)
                    continue;
                if (cacheEnt.isLost())
                    lostList.add(ent.getKey());
            }
            /*移除失效的元素*/
            if (lostList.isEmpty() == false) {
                for (String lostKey : lostList)
                    this.cacheEntityMap.remove(lostKey);
            }
            this.cacheEntityLock.writeLock().unlock();//解锁(写)
            try {
                sleep(settings.getThreadSeep());
            } catch (InterruptedException e) {}
        }
    }
    @Override
    public synchronized void initCache(AppContext appContext) {
        this.settings = appContext.getInstance(MapCacheSettings.class);
        this.exitThread = false;
        this.setDaemon(true);
        this.start();
    }
    @Override
    public synchronized void destroy(AppContext appContext) {
        this.exitThread = true;
        this.clear();
    }
    @Override
    public boolean toCache(String key, T value) {
        return this.toCache(key, value, this.settings.getDefaultTimeout());
    }
    @Override
    public boolean toCache(String key, T value, long timeout) {
        if (key == null)
            return false;
        this.cacheEntityLock.writeLock().lock();//加锁(写)
        if (this.settings.isEternal() == true) {
            timeout = Long.MAX_VALUE;
        } else if (timeout <= 0)
            timeout = this.settings.getDefaultTimeout();
        CacheEntity<T> oldEnt = this.cacheEntityMap.put(key, new CacheEntity<T>(value, timeout));
        this.cacheEntityLock.writeLock().unlock();//解锁(写)
        return true;
    }
    @Override
    public T fromCache(String key) {
        T returnData = null;
        this.cacheEntityLock.readLock().lock();//加锁(读)
        CacheEntity<T> cacheEntity = this.cacheEntityMap.get(key);
        if (cacheEntity != null) {
            if (this.settings.isAutoRenewal() == true)
                cacheEntity.refresh();
            returnData = cacheEntity.get();
        }
        this.cacheEntityLock.readLock().unlock();//解锁(读)
        return returnData;
    }
    @Override
    public boolean hasCache(String key) {
        this.cacheEntityLock.readLock().lock();//加锁(读)
        boolean res = this.cacheEntityMap.containsKey(key);
        this.cacheEntityLock.readLock().unlock();//解锁(读)
        return res;
    }
    @Override
    public boolean remove(String key) {
        this.cacheEntityLock.writeLock().lock();//加锁(写)
        CacheEntity<T> cacheEntity = this.cacheEntityMap.remove(key);
        this.cacheEntityLock.writeLock().unlock();//解锁(写)
        return cacheEntity != null;
    }
    @Override
    public boolean refreshCache(String key) {
        this.cacheEntityLock.readLock().lock();//加锁(读)
        CacheEntity<T> cacheEntity = this.cacheEntityMap.get(key);
        if (cacheEntity != null)
            cacheEntity.refresh();
        this.cacheEntityLock.readLock().unlock();//解锁(读)
        return cacheEntity != null;
    }
    @Override
    public boolean clear() {
        this.cacheEntityLock.writeLock().lock();//加锁(写)
        this.cacheEntityMap.clear();
        this.cacheEntityLock.writeLock().unlock();//解锁(写)
        return true;
    }
    /*-------------------------------------------------------------------------------------*/
    private static class CacheEntity<T> {
        private volatile T    value    = null;
        private volatile long timeout  = 0;
        private volatile long lastTime = 0;
        //
        public CacheEntity(T value, long timeout) {
            this.value = value;
            this.timeout = timeout;
            this.lastTime = System.currentTimeMillis();
        }
        //
        public boolean isLost() {
            if (this.timeout == Long.MAX_VALUE)
                return false;
            return (lastTime + this.timeout) < System.currentTimeMillis();
        }
        //
        public void refresh() {
            this.lastTime = System.currentTimeMillis();
        }
        //
        public T get() {
            return this.value;
        }
    }
}