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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 一个铁路，多辆列车
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class TrackManager {
    private TrainNode[]     trainArray   = null;
    private AtomicInteger[] trainSignal  = null;
    private Class<?>[]      stationArray = null;
    //
    public TrackManager(Class<?>[] stationArray) {
        this(stationArray, 2, 2048);
    }
    public TrackManager(Class<?>[] stationArray, int trainCount, int capacity) {
        /*初始化车站*/
        if (stationArray == null) {
            stationArray = new Class[0];
        }
        this.stationArray = stationArray;
        /*列车数*/
        if (trainCount <= 0) {
            trainCount = 1;
        }
        /*初始化列车&信号*/
        this.trainArray = new TrainNode[trainCount];
        this.trainSignal = new AtomicInteger[trainCount];
        for (int i = 0; i < trainCount; i++) {
            this.trainArray[i] = new TrainNode(capacity);
            this.trainSignal[i] = new AtomicInteger();
        }
        /*布局列车信号，保证每个车站都有列车出发*/
        int j = 0;
        for (int i = 0; i < trainCount; i++) {
            this.trainSignal[i].set(j++);
            if (j == stationArray.length)
                j = 0;
        }
    }
    //
    /**waitType 定是要追踪的类型，一旦等到了要追踪的类型将会返回。该方法会有很多线程调用，因此每个线程都在等待追踪的那个类型的到达。*/
    public TWrite waitForWrite(Class<?> waitType) {
        return waitFor(waitType, 1);
    }
    /**waitType 定是要追踪的类型，一旦等到了要追踪的类型将会返回。该方法会有很多线程调用，因此每个线程都在等待追踪的那个类型的到达。*/
    public TRead waitForRead(Class<?> waitType) {
        return waitFor(waitType, -1);
    }
    //
    private TrainNode waitFor(Class<?> waitType, int rw) {
        int atTrainNode = -1;
        Out: while (true) {
            for (int i = 0; i < this.trainSignal.length; i++) {
                //循环所有列车的信号量，找出正在位于 waitType 的那个列车
                boolean trainHit = this.stationArray[this.trainSignal[i].get()].equals(waitType);
                //找到那个列车
                if (trainHit && this.trainArray[i].takeFor(rw)) {
                    atTrainNode = i;
                    break Out;
                }
            }
            Thread.yield(); // 为保证高吞吐量的消息传递，这个是必须的,但在等待列车时它会消耗CPU周期
        }
        return markTrain(atTrainNode);
    }
    private ThreadLocal<List<Integer>> localMark = new ThreadLocal<List<Integer>>();
    private TrainNode markTrain(int train) {
        if (train < 0)
            return null;
        //
        List<Integer> trainList = this.localMark.get();
        if (trainList == null) {
            trainList = new ArrayList<Integer>(this.trainArray.length);
            this.localMark.set(trainList);
        }
        trainList.add(train);
        return this.trainArray[train];
    }
    /**通过这个方法将列车开往下一个目的地。*/
    public void switchNext(Class<?> waitType) {
        int switchTo = -1;
        for (int i = 0; i < this.stationArray.length; i++) {
            if (this.stationArray[i].equals(waitType))
                switchTo = i;
        }
        if (switchTo < 0)
            throw new RuntimeException("");
        //
        List<Integer> trainList = this.localMark.get();
        if (trainList != null) {
            for (int i = 0; i < trainList.size(); i++) {
                //1.车站轮转
                this.trainSignal[i].set(switchTo);
                //2.模式重置
                this.trainArray[trainList.get(i)].cleanTake();
            }
            trainList.clear();
        }
    }
}