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
package net.hasor.rsf._test.socket;
import net.hasor.rsf.executes.ExecutesManager;
import net.hasor.rsf.executes.MessageProcessing;
import net.hasor.rsf.protocol.message.RequestMsg;
/**
 * 
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerHandler implements Runnable {
    private static volatile long readCount = 0;
    private static volatile long start     = System.currentTimeMillis();
    private ExecutesManager      manager   = null;
    //
    public ServerHandler(ExecutesManager manager) {
        this.manager = manager;
        //        this.manager.addMessageProcessing(Object.class, this);
    }
    //
    //
    //
    public void run() {
        while (true) {}
        //
        if (msg instanceof RequestMsg == false)
            return;
        long requestID = ((RequestMsg) msg).getRequestID();
        //
        readCount++;
        //
        long duration = System.currentTimeMillis() - start;
        if (duration % 100 == 0) {
            long qps = readCount * 1000 / duration;
            System.out.println("QPS:" + qps);
            System.out.println("readCount:" + readCount);
            System.out.println("last REQID:" + requestID);
            System.out.println();
        }
    }
}