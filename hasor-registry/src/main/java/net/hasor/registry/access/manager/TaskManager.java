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
package net.hasor.registry.access.manager;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.registry.access.adapter.DataAdapter;
import net.hasor.registry.access.domain.LogUtils;
import net.hasor.registry.access.pusher.RsfPusher;
import net.hasor.rsf.domain.RsfServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 每当服务注册和离线的时候，都会生成一个服务地址推送任务通过 addTask 方法加入进来。
 * 但当遇到大量服务启动或者离线情况下，如果直接调用 RsfPusher 进行推送会造成大量的推送行为。
 *
 * TaskManager 解决这个问题的办法是，设立的一个临时的中转站。分批次将大量同类的推送优化合并成几个有限的批次。
 *
 * @version : 2016年9月18日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class TaskManager extends Thread {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private LinkedBlockingQueue<Task> taskQueue;
    @Inject
    private RsfPusher                 rsfPusher;
    @Inject
    private DataAdapter               dataAdapter;
    //
    @Init
    public void init() {
        this.taskQueue = new LinkedBlockingQueue<Task>();
        this.setDaemon(true);
        this.setName("RsfCenter-TaskToPusher");
        this.start();
    }
    public void asyncToPublish(String serviceID, Task task) {
        this.taskQueue.offer(task);
    }
    @Override
    public void run() {
        logger.info("taskToPusher Thread start.");
        while (true) {
            try {
                Task task = null;
                while ((task = this.taskQueue.take()) != null) {
                    if (task instanceof PublishTask) {
                        this.appendAddress(task.serviceID, task.addressList);
                        break;
                    }
                    if (task instanceof RemoveTask) {
                        this.invalidAddress(task.serviceID, task.addressList);
                        break;
                    }
                }
            } catch (Throwable e) {
                logger.error(LogUtils.create("ERROR_300_00004")//
                        .logException(e).toJson());
            }
        }
    }
    //
    //
    private void invalidAddress(String serviceID, List<String> invalidAddressSet) {
        // .获取服务的消费者列表
        final int rowCount = this.dataAdapter.getPointCountByServiceID(serviceID, RsfServiceType.Consumer);
        final int limitSize = 100;
        int rowIndex = 0;
        while (rowIndex <= rowCount) {
            List<String> targetList = this.dataAdapter.getPointByServiceID(serviceID, RsfServiceType.Consumer, rowIndex, limitSize);
            rowIndex = rowIndex + limitSize;
            if (targetList == null || targetList.isEmpty()) {
                continue;
            }
            // .推送失效地址
            boolean result = this.rsfPusher.removeAddress(serviceID, invalidAddressSet, targetList); // 第一次尝试
            if (!result) {
                result = this.rsfPusher.removeAddress(serviceID, invalidAddressSet, targetList);     // 第二次尝试
                if (!result) {
                    result = this.rsfPusher.removeAddress(serviceID, invalidAddressSet, targetList); // 第三次尝试
                }
            }
        }
        //
    }
    private void appendAddress(String serviceID, List<String> newAddressSet) {
        // .获取服务的消费者列表
        final int rowCount = this.dataAdapter.getPointCountByServiceID(serviceID, RsfServiceType.Consumer);
        final int limitSize = 100;
        int rowIndex = 0;
        while (rowIndex <= rowCount) {
            List<String> targetList = this.dataAdapter.getPointByServiceID(serviceID, RsfServiceType.Consumer, rowIndex, limitSize);
            rowIndex = rowIndex + limitSize;
            if (targetList == null || targetList.isEmpty()) {
                continue;
            }
            // .推送失效地址
            boolean result = this.rsfPusher.appendAddress(serviceID, newAddressSet, targetList); // 第一次尝试
            if (!result) {
                result = this.rsfPusher.appendAddress(serviceID, newAddressSet, targetList);     // 第二次尝试
                if (!result) {
                    result = this.rsfPusher.appendAddress(serviceID, newAddressSet, targetList); // 第三次尝试
                }
            }
        }
        //
    }
    //
    /** 任务 */
    public static class Task {
        public final String       serviceID;
        public final List<String> addressList;
        public Task(String serviceID, List<String> addressList) {
            this.serviceID = serviceID;
            this.addressList = addressList;
        }
    }
    /** 增量推送服务地址给全部消费者 or 特定机器 */
    public static class PublishTask extends Task {
        public PublishTask(String serviceID, List<String> addressList) {
            super(serviceID, addressList);
        }
    }
    /** 增量推送失效的地址 */
    public static class RemoveTask extends Task {
        public RemoveTask(String serviceID, List<String> addressList) {
            super(serviceID, addressList);
        }
    }
}