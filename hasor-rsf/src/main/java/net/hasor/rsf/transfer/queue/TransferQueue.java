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
package net.hasor.rsf.transfer.queue;
import net.hasor.rsf.transfer.TRead;
import net.hasor.rsf.transfer.TWrite;
import net.hasor.rsf.transfer.TrackManager;
/**
 * 由于操作 Thread Local 性能有很大的下降。
 * @version : 2014年11月6日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransferQueue {
    private TrackManager track = null;
    //
    public TransferQueue() {
        this(10, 2048);
    }
    public TransferQueue(int trainCount, int capacity) {
        this.track = new TrackManager(QueueEnum.values(), trainCount, capacity);
        this.lock = new ThreadLocal<Boolean>() {
            protected Boolean initialValue() {
                return false;
            }
        };
    }
    private static enum QueueEnum {
        A
    };
    //
    private ThreadLocal<Boolean> lock  = null;
    private ThreadLocal<TWrite>  wNode = new ThreadLocal<TWrite>();
    private ThreadLocal<TRead>   rNode = new ThreadLocal<TRead>();
    /**加锁*/
    public void lock() {
        if (this.lock.get() == true)
            return;
        this.wNode.set(this.track.waitForWrite(QueueEnum.A));
        this.rNode.set(this.track.waitForRead(QueueEnum.A));
        this.lock.set(true);
    }
    /**解锁*/
    public void unlock() {
        this.track.switchNext(QueueEnum.A);
        this.wNode.remove();
        this.rNode.remove();
        this.lock.set(false);
    }
    //
    /**推进去*/
    public void push(Object good) {
        lock();
        TWrite w = this.wNode.get();
        if (w.isFull()) {
            w = this.track.waitForWrite(QueueEnum.A);
            this.wNode.set(w);
        }
        w.pushGood(good);
    }
    /**拉出来*/
    public Object pull() {
        lock();
        TRead r = this.rNode.get();
        if (r.isEmpty()) {
            r = this.track.waitForRead(QueueEnum.A);
            this.rNode.set(r);
        }
        return r.pullGood();
    }
}