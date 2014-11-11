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
package net.hasor.rsf._test.transfer;
import java.io.IOException;
import net.hasor.rsf.executes.queue.TRead;
import net.hasor.rsf.executes.queue.TWrite;
import net.hasor.rsf.executes.queue.TrackManager;
/**
 * 
 * @version : 2014年11月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransferTest_2 {
    static enum StationEnum {
        A, B, C, D
    };
    //
    //
    private static final int CAPACITY = 4096;
    static volatile long     index    = 0;
    public static void main(String[] args) throws Exception {
        _1_main(args);
        //
        //性能统计
        final long start = System.currentTimeMillis();
        while (true) {
            Thread.sleep(2000);
            final long duration = System.currentTimeMillis() - start;
            final long ops = (index * 1000) / duration;
            System.out.format("ops/sec = %,d\n", ops);
            System.out.format("trains/sec = %,d\n", ops / CAPACITY);
            System.out.format("\n");
            //            System.out.format("latency nanos = %.3f%n\n", duration / (float) (index) * (float) CAPACITY);
        }
    }
    public static void _1_main(String[] args) throws IOException {
        final TrackManager t = new TrackManager(//
                StationEnum.values(), 40, CAPACITY);
        //
        seller(StationEnum.A, StationEnum.B, t, 0).start();
        consumer(StationEnum.B, StationEnum.C, t, 0).start();
        consumer(StationEnum.B, StationEnum.C, t, 0).start();
        consumer(StationEnum.B, StationEnum.C, t, 0).start();
        consumer(StationEnum.B, StationEnum.C, t, 0).start();
        consumer(StationEnum.B, StationEnum.C, t, 0).start();
        consumer(StationEnum.B, StationEnum.C, t, 0).start();
        //
        seller(StationEnum.C, StationEnum.D, t, 0).start();
        consumer(StationEnum.D, StationEnum.A, t, 0).start();
        consumer(StationEnum.D, StationEnum.A, t, 0).start();
        consumer(StationEnum.D, StationEnum.A, t, 0).start();
        consumer(StationEnum.D, StationEnum.A, t, 0).start();
        consumer(StationEnum.D, StationEnum.A, t, 0).start();
        consumer(StationEnum.D, StationEnum.A, t, 0).start();
        consumer(StationEnum.D, StationEnum.A, t, 0).start();
    }
    private static Thread consumer(final StationEnum atStation, final StationEnum nextStation, final TrackManager track, final int id) {
        return new Thread() {
            public void run() {
                this.setName("Consumer-" + id);
                while (true) {
                    TRead node = track.waitForRead(atStation);
                    for (int i = 0; i < CAPACITY; i++) {
                        Object data = node.pullGood();
                    }
                    track.switchNext(nextStation);
                }
            }
        };
    }
    private static Thread seller(final StationEnum atStation, final StationEnum nextStation, final TrackManager track, final int id) {
        return new Thread() {
            public void run() {
                this.setName("Seller-" + id);
                while (true) {
                    TWrite node = track.waitForWrite(atStation);
                    for (int i = 0; i < CAPACITY; i++) {
                        node.pushGood(index++);
                    }
                    track.switchNext(nextStation);
                }
            }
        };
    }
}
