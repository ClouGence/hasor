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
package test.net.hasor.rsf.route.flowcontrol;
import net.hasor.rsf.route.flowcontrol.speed.QoSBucket;
import org.junit.Test;
/**
 * 
 * @version : 2015年4月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class QosTest {
    @Test
    public void bucketTest() throws Throwable {
        //QoS：稳定每秒50，峰值1000，时间更新窗口10毫秒。
        QoSBucket qos = new QoSBucket(50, 1000, 10);
        //
        int i = 0;
        long startTime = System.currentTimeMillis() / 1000;
        Thread.sleep(1000);
        while (true) {
            if (qos.check() == true) {
                i++;
                long checkTime = System.currentTimeMillis() / 1000;
                System.out.println("Count:" + i + "\tSpeed(s):" + (i / (checkTime - startTime)));
            }
        }
    }
}