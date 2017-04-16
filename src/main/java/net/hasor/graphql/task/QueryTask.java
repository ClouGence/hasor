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
package net.hasor.graphql.task;
import java.util.concurrent.ExecutionException;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public interface QueryTask {
    /** 打印执行任务树 */
    public String printTaskTree(boolean detail);

    /**获取执行结果，如果任务尚未调度，那么会抛出异常。*/
    public Object getValue() throws ExecutionException, InterruptedException;

    /** 任务节点是否执行完毕(包含成功和失败) */
    public boolean isFinish();

    /** 任务是否准备就绪，以等待分配执行资源 */
    public boolean isWaiting();
}