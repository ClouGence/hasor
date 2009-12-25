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
import org.more.InvokeException;
/**
 * 扩展外壳生成器，submit2.0的任何外壳扩展都需要继承这个生成器用于生成新的外壳所使用。
 * 这个生成器中生成的ActionFactory对象是用于管理action的容器。而FilterFactory是
 * 用于管理action的过滤器容器。
 * Date : 2009-6-30
 * @author 赵永春
 */
public abstract class CasingBuild {
    protected Config config = null; //配置信息
    /**
     * 初始化生成器并且传递初始化参数。
     * @param config 初始化参数对象。
     * @throws InvokeException 如果在初始化过程中出现异常则炮出该类异常。
     */
    public void init(Config config) throws InvokeException {
        this.config = config;
    }
    /**
     * 获取配置对象。该方法只有在继承CasingBuild类之后才能被调用。
     * @return 返回配置对象。该方法只有在继承CasingBuild类之后才能被调用。
     */
    protected Config getConfig() {
        return config;
    }
    /**
     * 创建submit2.0外壳扩展的一个必须组建Action管理器。sbumit通过ActionFactory查找获取action对象。
     * @return 返回创建submit2.0外壳扩展的一个必须组建Action管理器。
     */
    public abstract ActionFactory getActionFactory();
    /**
     * 创建submit2.0外壳扩展的一个必须组建Action过滤器管理器。sbumit通过FilterFactory查找获取action对象的装配过滤器。
     * @return 返回创建submit2.0外壳扩展的一个必须组建Action过滤器管理器。
     */
    public abstract FilterFactory getFilterFactory();
}