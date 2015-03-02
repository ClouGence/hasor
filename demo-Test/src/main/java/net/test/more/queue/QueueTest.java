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
package net.test.more.queue;
import net.test.other.queue.TRead;
import net.test.other.queue.TWrite;
import net.test.other.queue.TrackManager;
import org.junit.Test;
/**
 * 
 * @version : 2015年3月2日
 * @author 赵永春(zyc@hasor.net)
 */
public class QueueTest {
    @Test
    public void queueTest() throws Throwable {
        //无锁队列
        TrackManager<String> track = new TrackManager<String>(TaskEnum.class);
        //
        //Work线程
        TaskProcess work1 = new TaskProcess(track);
        TaskProcess work2 = new TaskProcess(track);
        TaskProcess work3 = new TaskProcess(track);
        TaskProcess work4 = new TaskProcess(track);
        work1.start();
        work2.start();
        work3.start();
        work4.start();
        //
        int i = 0;
        while (true) {
            i++;
            String goodStr = "Good_" + i;
            //
            while (true) {
                //等待一辆可以装货的列车
                TWrite<String> tw = track.waitForWrite(TaskEnum.TaskA);
                //尝试把任务装到列车上
                boolean res = tw.pushGood(goodStr);
                //让列车驶向下一站（消费任务）
                track.switchNext(TaskEnum.TaskB);
                //是否等待下一辆列车来装载此货物
                if (res == true)
                    break;
                Thread.sleep(100);
            }
        }
    }
}
enum TaskEnum {
    TaskA, TaskB
}
class TaskProcess extends Thread {
    private TrackManager<String> track;
    public TaskProcess(TrackManager<String> track) {
        this.track = track;
    }
    public void run() {
        while (true) {
            //等待可以卸货的列车
            TRead<String> tr = track.waitForRead(TaskEnum.TaskA);
            //卸货
            String task = tr.pullGood();
            if (task != null) {
                System.out.println(this.getId() + ":\t" + task);
            }
            //开动列车驶向下一站（装载任务）
            track.switchNext(TaskEnum.TaskB);
        }
    }
}