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
package net.hasor.rsf.center.core.zookeeper;
import java.io.IOException;
import org.apache.zookeeper.ZooKeeper;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public interface ZooKeeperNode {
    /** ZK系统中的基准节点 */
    public static final String ROOT_PATH = "/rsf-center";
    //
    /** 终止ZooKeeper */
    public void shutdownZooKeeper() throws IOException, InterruptedException;
    /** 启动ZooKeeper */
    public void startZooKeeper() throws IOException, InterruptedException;
    /** 返回ZK */
    public ZooKeeper getZooKeeper();
}