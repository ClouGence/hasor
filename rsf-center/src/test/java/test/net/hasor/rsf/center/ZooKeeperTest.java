/*
 * Copyright 2008-2009 the original author or authors.
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
package test.net.hasor.rsf.center;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
/**
 * @version : 2015年8月13日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperTest implements Watcher {
    private static CountDownLatch connectedSemphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception {
        // 创建Zookeeper会话实例
        ZooKeeper zookeeper = new ZooKeeper("30.10.229.204:2181", 5000, new ZooKeeperTest());
        // 输出当前会话的状态
        System.out.println("zk客户端的状态是：" + zookeeper.getState());
        System.out.println("zk 客户端的sessionId=" + zookeeper.getSessionId() + ",  sessionPasswd是：" + new String(zookeeper.getSessionPasswd()));
        try {
            // 当前闭锁在为0之前一直等待，除非线程中断
            connectedSemphore.await();
        } catch (Exception e) {
            System.out.println("Zookeeper session established");
        }
        //
        // zookeeper.create("/rsf-center/hosts", "1", acl, createMode)
        //
        //
        zookeeper.close();
    }
    /**
     * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
     */
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event:" + event);
        // 如果客户端已经处于连接状态闭锁减去1
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemphore.countDown();
        }
    }
}