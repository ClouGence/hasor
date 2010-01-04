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
/**
 * 扩展外壳生成器，submit的任何外壳扩展都需要继承这个生成器用于生成新的外壳所使用。
 * 这个抽象类的子类通过实现getActionFactory方法来生成{@link ActionContext ActionContext接口}对象。
 * <br/>Date : 2009-12-2
 * @author 赵永春
 */
public abstract class CasingBuild {
    //========================================================================================Field
    protected Config      config             = null; //配置信息
    private ActionContext cacheActionContext = null;
    private boolean       cacheContext       = true; //是否缓存
    //==========================================================================================Job
    /**
     * 初始化生成器并且传递初始化参数。
     * @param config 初始化参数对象。
     */
    public void init(Config config) throws Exception {
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
     * 创建submit外壳扩展的一个必须组建Action管理器。sbumit通过ActionContext查找获取action对象。
     * @return 返回创建submit外壳扩展的一个必须组建Action管理器。
     */
    public ActionContext getActionFactory() {
        if (cacheContext == false)
            return this.createActionContext();
        if (this.cacheActionContext == null)
            this.cacheActionContext = this.createActionContext();
        return this.cacheActionContext;
    }
    /**获取CasingBuild对象是否缓存ActionFactory对象。*/
    public boolean isCacheContext() {
        return cacheContext;
    }
    /**设置一个值，该值决定了当调用getActionFactory方法生产的ActionContext对象是否缓存起来为下一次调用getActionFactory方法而使用。true表示缓存,false表示不缓存。*/
    public void setCacheContext(boolean cacheContext) {
        this.cacheContext = cacheContext;
    }
    protected abstract ActionContext createActionContext();
}