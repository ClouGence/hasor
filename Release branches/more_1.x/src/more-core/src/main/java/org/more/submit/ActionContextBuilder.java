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
package org.more.submit;
import org.more.util.config.Config;
/**
 * 该接口是用于生成{@link ActionContext}接口的生成器，在该接口中可以得到更多的容器支持。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionContextBuilder {
    /**容器传递进来的配置信息。*/
    public void init(Config<?> config);
    /**生成{@link ActionContext}接口对象。*/
    public ActionContext builder() throws Throwable;
    /**获取要注册的命名空间。*/
    public String getPrefix();
}