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
package net.test.other.queue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 一个铁路，多辆列车
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class TrackManager<T> {
    private TrainNode<T>[]  trainArray   = null; //多辆列车
    private AtomicInteger[] trainSignal  = null; //列车所处车站
    private Enum<?>[]       stationArray = null; //所有车站
    //
    public TrackManager(Class<? extends Enum<?>> stationEnum) {
        this(stationEnum, 4, 2);
    }
    public TrackManager(Class<? extends Enum<?>> stationEnum, int trainCount, int capacity) {
        /*初始化车站*/
        this.stationArray = stationEnum.getEnumConstants();
        /*列车数*/
        if (trainCount <= 0) {
            trainCount = 1;
        }
        /*初始化列车&信号*/
        this.trainArray = new TrainNode[trainCount];
        this.trainSignal = new AtomicInteger[trainCount];
        for (int i = 0; i < trainCount; i++) {
            this.trainArray[i] = new TrainNode<T>(capacity);
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
    public TWrite<T> waitForWrite(Enum<?> waitStation) {
        return waitFor(waitStation, 1);
    }
    /**waitType 定是要追踪的类型，一旦等到了要追踪的类型将会返回。该方法会有很多线程调用，因此每个线程都在等待追踪的那个类型的到达。*/
    public TRead<T> waitForRead(Enum<?> waitStation) {
        return waitFor(waitStation, -1);
    }
    //
    private TrainNode<T> waitFor(Enum<?> waitStation, int rw) {
        int atTrainNode = -1;
        Out: while (true) {
            for (int i = 0; i < this.trainSignal.length; i++) {
                //循环所有列车的信号量，找出正在位于 waitType 的那个列车
                boolean trainHit = this.stationArray[this.trainSignal[i].get()] == waitStation;
                //找到那个列车
                if (trainHit && this.trainArray[i].takeFor(rw)) {
                    atTrainNode = i;
                    break Out;
                }
            }
            //
            try {
//                Thread.yield(); // 为保证高吞吐量的消息传递，这个是必须的,但在等待列车时它会消耗CPU周期
                Thread.sleep(1);
            } catch (Exception e) {}
        }
        return markTrain(atTrainNode);
    }
    private ThreadLocal<List<Integer>> localMark = new ThreadLocal<List<Integer>>();
    private TrainNode<T> markTrain(int train) {
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
    //
    /**通过这个方法将列车开往下一个目的地。*/
    public void switchNext(Enum<?> nextStation) {
        int switchTo = -1;
        for (int i = 0; i < this.stationArray.length; i++) {
            if (this.stationArray[i] == nextStation)
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
    /**是否所有列车上都没有货物了*/
    public boolean isEmpty() {
        for (TrainNode<T> train : trainArray)
            if (train.isEmpty() == false)
                return false;
        return true;
    }
    //
    /**
     * 在某一个车站等待列车，当有列车到达之后，装上货物让列车驶向指定的下一站。
     * @param waitStation 等待的车站
     * @param nextStation 目标车站
     * @param good 货物
     */
    public void waitForWrite(Enum<?> waitStation, Enum<?> nextStation, T good) {
        while (true) {
            //等待一辆可以装货的列车
            TWrite<T> tw = this.waitForWrite(waitStation);
            //尝试把任务装到列车上
            boolean res = tw.pushGood(good);
            //让列车驶向下一站（消费任务）
            this.switchNext(nextStation);
            //是否等待下一辆列车来装载此货物
            if (res == true)
                break;
            try {
                Thread.sleep(1);
            } catch (Exception e) { }
            //
        }
    }
    /**
     * 在某一个车站等待列车，直到有货物到达。
     * @param waitStation 等待的车站
     * @param nextStation 经过列车的下一个车站
     */
    public T waitForRead(Enum<?> waitStation, Enum<?> nextStation) {
        while (true) {
            TRead<T> tr = this.waitForRead(waitStation);
            T task = tr.pullGood();
            this.switchNext(nextStation);
            if (task != null) {
                return task;
            }
            try {
                Thread.sleep(1);
            } catch (Exception e) { }
            //
        }
    }
}