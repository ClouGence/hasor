/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.transfer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 列车
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class TrainNode implements TRead, TWrite {
    private int poolCapacity = 2048;
    private class GoodPool {
        private Object[] dataPool   = new Object[poolCapacity];
        private int      readIndex  = 0;
        private int      writeIndex = 0;
        private int      size       = 0;
    }
    //
    private AtomicInteger           model   = new AtomicInteger(0); //-1(读)，0(就绪)，1(写)
    private Map<Class<?>, GoodPool> goodMap = null;
    //
    public TrainNode() {
        this(2048);
    }
    public TrainNode(int capacity) {
        this.goodMap = new HashMap<Class<?>, GoodPool>();
        this.poolCapacity = capacity;
    }
    //
    /**判断节点上是否有货物。*/
    public boolean hasGoods(Class<?> goodType) {
        return this.goodMap.containsKey(goodType);
    }
    public int getGoodCount(Class<?> goodType) {
        GoodPool pool = this.goodMap.get(goodType);
        if (pool == null) {
            return 0;
        }
        return pool.size;
    }
    /**列车容量*/
    public int getCapacity() {
        return this.poolCapacity;
    }
    /**货物是否装满了*/
    public boolean isFull(Class<?> goodType) {
        GoodPool pool = this.goodMap.get(goodType);
        if (pool == null) {
            return false;
        }
        return pool.size == pool.dataPool.length;
    }
    /**推送一个货物到节点上。*/
    public boolean pushGood(Class<?> goodType, Object good) {
        if (good == null)
            return false;
        //
        GoodPool pool = this.goodMap.get(goodType);
        if (pool == null) {
            pool = new GoodPool();
            this.goodMap.put(goodType, pool);
        }
        //
        if (pool.size == pool.dataPool.length)
            return false;
        //
        pool.dataPool[pool.writeIndex++] = good;
        pool.size++;
        //
        if (pool.writeIndex == pool.dataPool.length)
            pool.writeIndex = 0;
        //
        return true;
    }
    /**从节点中拉取一个货物。*/
    public <T> T pullGood(Class<T> goodType) {
        GoodPool pool = this.goodMap.get(goodType);
        if (pool == null) {
            return null;
        }
        //
        Object good = pool.dataPool[pool.readIndex];
        pool.dataPool[pool.readIndex] = null;
        pool.readIndex++;
        pool.size--;
        // 
        if (pool.readIndex == pool.dataPool.length)
            pool.readIndex = 0;
        //
        return (T) good;
    }
    //
    public void cleanTake() {
        this.model.set(0);
    }
    /**-1(读)，0(就绪)，1(写)*/
    public boolean takeFor(int rw) {
        return this.model.compareAndSet(0, rw);
    }
}