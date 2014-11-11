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
import net.hasor.rsf.transfer.queue.TransferQueue;
/**
 * 
 * @version : 2014年11月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransferTest_3 {
    //
    //
    private static final int CAPACITY = 20;
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
        final TransferQueue queue = new TransferQueue(2048, CAPACITY);
        //
        seller(queue, 0).start();
        consumer(queue, 0).start();
    }
    private static Thread consumer(final TransferQueue queue, final int id) {
        return new Thread() {
            public void run() {
                this.setName("Consumer-" + id);
                while (true) {
                    for (int i = 0; i < CAPACITY; i++) {
                        Object data = queue.pull();
                    }
                    queue.unlock();
                }
            }
        };
    }
    private static Thread seller(final TransferQueue queue, final int id) {
        return new Thread() {
            public void run() {
                this.setName("Seller-" + id);
                while (true) {
                    for (int i = 0; i < CAPACITY; i++) {
                        queue.push(index++);
                    }
                    queue.unlock();
                }
            }
        };
    }
}
