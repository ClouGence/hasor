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
    private TrainNode[]   trainArray     = null;
    private Class<?>[]    stationArray   = null;
    private AtomicInteger currentStation = null;
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
        /*初始化列车*/
        this.trainArray = new TrainNode[trainCount];
        for (int i = 0; i < trainCount; i++) {
            this.trainArray[i] = new TrainNode(capacity);
        }
        /*初始化追踪信号*/
        this.currentStation = new AtomicInteger();
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
        int trainCount = this.trainArray.length;
        TrainNode atTrainNode = null;
        //
        Out: while (atTrainNode == null || !this.stationArray[currentStation.get()].equals(waitType)) {
            //从众多列车中选出一辆可以卸货的列车。
            for (int i = 0; i < trainCount; i++) {
                if (this.trainArray[i].takeFor(rw)) {
                    atTrainNode = this.trainArray[i];
                    break Out;
                }
            }
            Thread.yield(); // 为保证高吞吐量的消息传递，这个是必须的,但在等待列车时它会消耗CPU周期
        }
        //
        return markTrain(atTrainNode);
    }
    private ThreadLocal<List<TrainNode>> localMark = new ThreadLocal<List<TrainNode>>();
    private TrainNode markTrain(TrainNode train) {
        List<TrainNode> trainList = this.localMark.get();
        if (trainList == null) {
            trainList = new ArrayList<TrainNode>(this.trainArray.length);
            this.localMark.set(trainList);
        }
        trainList.add(train);
        return train;
    }
    /**通过这个方法轮转追踪的类型。*/
    public void switchNext() {
        //1.模式重置
        List<TrainNode> trainList = this.localMark.get();
        if (trainList != null) {
            for (int i = 0; i < trainList.size(); i++)
                trainList.get(i).cleanTake();
            trainList.clear();
        }
        //2.车站轮转
        if (this.currentStation.compareAndSet(this.stationArray.length - 1, 0) == false) {
            this.currentStation.getAndIncrement();
        }
    }
}