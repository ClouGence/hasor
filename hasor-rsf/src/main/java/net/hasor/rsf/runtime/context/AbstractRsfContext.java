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
package net.hasor.rsf.runtime.context;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.Executor;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfFilter;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext {
    /**获取使用的{@link EventLoopGroup}*/
    public abstract EventLoopGroup getLoopGroup();
    /**获取{@link Executor}用于安排执行任务。*/
    public abstract Executor getCallExecute(String serviceName);
    /**获取服务上配置有效的过滤器。*/
    public abstract RsfFilter[] getRsfFilters(ServiceMetaData metaData);
}