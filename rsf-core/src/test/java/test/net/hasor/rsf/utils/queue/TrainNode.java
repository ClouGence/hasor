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
package test.net.hasor.rsf.utils.queue;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 列车
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class TrainNode implements TRead, TWrite {
    private int           poolCapacity = 2048;
    private Object[]      dataPool     = new Object[0];
    private int           readIndex    = 0;
    private int           writeIndex   = 0;
    private int           size         = 0;
    private AtomicInteger rwModel      = new AtomicInteger(0); //-1(读)，0(就绪)，1(写)
    //
    public TrainNode() {
        this(2048);
    }
    public TrainNode(int capacity) {
        this.poolCapacity = capacity;
        this.dataPool = new Object[capacity];
    }
    //
    public int getGoodCount() {
        return this.size;
    }
    /**列车容量*/
    public int getCapacity() {
        return this.poolCapacity;
    }
    /**货物是否装满了*/
    public boolean isFull() {
        return this.size == this.dataPool.length;
    }
    /**货物是否空了*/
    public boolean isEmpty() {
        return this.size == 0;
    }
    /**推送一个货物到节点上。*/
    public boolean pushGood(Object good) {
        if (good == null)
            return false;
        //
        if (this.size == this.dataPool.length)
            return false;
        //
        this.dataPool[this.writeIndex++] = good;
        this.size++;
        //
        if (this.writeIndex == this.dataPool.length)
            this.writeIndex = 0;
        //
        return true;
    }
    /**从节点中拉取一个货物。*/
    public Object pullGood() {
        Object good = this.dataPool[this.readIndex];
        this.dataPool[this.readIndex] = null;
        this.readIndex++;
        this.size--;
        // 
        if (this.readIndex == this.dataPool.length)
            this.readIndex = 0;
        //
        return good;
    }
    //
    public void cleanTake() {
        this.rwModel.set(0);
    }
    /**-1(读)，0(就绪)，1(写)*/
    public boolean takeFor(int rw) {
        return this.rwModel.compareAndSet(0, rw);
    }
}