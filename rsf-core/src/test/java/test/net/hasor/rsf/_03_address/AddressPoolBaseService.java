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
package test.net.hasor.rsf._03_address;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import net.hasor.rsf.address.AddressPool;
import net.hasor.rsf.address.InterAddress;
/**
 * 
 * @version : 2015年4月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class AddressPoolBaseService {
    /*用于计算速率的数据*/
    public class TimeData {
        long       startTime = System.currentTimeMillis();
        AtomicLong atomicValue;
    }
    /*负责不停的执行doNextAddress的线程。*/
    public class NextWork implements Runnable {
        private AddressPool                           pool;
        private ConcurrentMap<InterAddress, TimeData> atomicMap;
        private String                                serviceID;
        private String                                methodName;
        private Object[]                              args;
        public NextWork(String serviceID, String methodName, Object[] args, AddressPool pool, ConcurrentMap<InterAddress, TimeData> atomicMap) {
            this.serviceID = serviceID;
            this.methodName = methodName;
            this.args = args;
            this.pool = pool;
            this.atomicMap = atomicMap;
        }
        public void run() {
            while (true) {
                doNextAddress(serviceID, methodName, args, this.pool, this.atomicMap);
            }
        }
    }
    /*负责打印atomicMap的线程*/
    public class MonitorWork implements Runnable {
        private ConcurrentMap<InterAddress, TimeData> atomicMap;
        public MonitorWork(ConcurrentMap<InterAddress, TimeData> atomicMap) {
            this.atomicMap = atomicMap;
        }
        public void run() {
            long lastTime = System.currentTimeMillis();
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {}
                long checkTime = System.currentTimeMillis();
                if (checkTime - lastTime < 1500) {
                    continue;//1.5秒打印一条
                }
                lastTime = System.currentTimeMillis();
                long invokeCountSum = 0;
                long invokeSpeedSum = 0;
                for (Entry<InterAddress, TimeData> entry : atomicMap.entrySet()) {
                    TimeData timeData = entry.getValue();
                    long invokeCount = timeData.atomicValue.get();
                    long invokeSpeed = eval(checkTime, timeData, invokeCount);
                    invokeCount = invokeCount / 10000;
                    invokeSpeed = invokeSpeed / 10000;
                    System.out.println(entry.getKey() + " - [Count/Speed](单位:万)\t" + invokeCount + "/" + invokeSpeed);
                    invokeCountSum = invokeCountSum + invokeCount;
                    invokeSpeedSum = invokeSpeedSum + invokeSpeed;
                }
                System.out.println("CountSum/SpeedSum(单位:万)\t" + invokeCountSum + "/" + invokeSpeedSum);
                System.out.println("------------------------");
                System.out.println();
            }
        }
        protected long eval(long checkTime, TimeData timeData, long invokeCount) {
            try {
                return invokeCount / ((checkTime - timeData.startTime) / 1000);
            } catch (Exception e) {
                return 0;//除零。
            }
        }
    }
    //
    public void doNextAddress(String serviceID, String methodName, Object[] args, AddressPool pool, Map<InterAddress, TimeData> atomicMap) {
        InterAddress inter = pool.nextAddress(serviceID, methodName, args);
        if (inter == null) {
            return;
        }
        TimeData atomicData = atomicMap.get(inter);
        if (atomicData == null) {
            atomicData = new TimeData();
            atomicData.atomicValue = new AtomicLong(0);
            atomicMap.put(inter, atomicData);
        }
        atomicData.atomicValue.getAndIncrement();//++
    }
}