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
package org.more.workflow.runtime;
import org.more.workflow.context.RunContext;
import org.more.workflow.util.Config;
/**
 * 流程节点运行时接口。在系统中所有节点的执行都需要通过最终的运行时来实现，在More WorkFlow系统中节点负责表述业务
 * 而运行时负责业务代码的执行。对于业务而言可以是一个外部应用程序调用，也可以是业务逻辑处理。也可以是消息的发送。<br/>
 * 每种不用类型的业务都对应他们有一个与之匹配的运行时。这个运行时负责了这一种业务的运行支撑，比方说Java调用业务的
 * 运行时就是负责调用Java代码。而Email运行时就是负责收发Email邮件的。<br/>
 * 提示：init和destroy方法由系统线程负责调用，beforeRun,doRun,afterRun三个方法的调用可能与与前两个方法所使用的线程不同。
 * Date : 2010-6-14
 * @author 赵永春
 */
public interface Runtime {
    /**当运行时被创建之后该方法就会被调用，该方法可以用于初始化运行时。通过参数可以获取到配置参数以及系统上下文对象。*/
    public void init(Config config) throws Throwable;
    /**doRun被执行之前调用，该方法可以用于准备运行时调用之前的一些资源对象。同时该方法也会在节点的process方法调用之前进行调用。*/
    public void beforeRun(Config param, RunContext runContext) throws Throwable;
    /**当节点的process方法被调用时，节点会自动调用该方法用于执行节点业务逻辑，对于不用的runtime会产生不同的执行动作。*/
    public Object doRun(Config param, RunContext runContext) throws Throwable;
    /**doRun被执行之后调用，利用该方法可以回收进行doRun的一些后续处理同时beforeRun分配的资源也可以在这里回收。同时该方法也会在节点的process方法调用之后进行调用。*/
    public void afterRun(Config param, RunContext runContext) throws Throwable;
    /**当没有任何节点使用该runtime时可能会被系统所回收，只有runtime被回收时才会调度该方法。*/
    public void destroy();
};