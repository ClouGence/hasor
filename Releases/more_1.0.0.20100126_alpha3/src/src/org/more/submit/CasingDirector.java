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
import java.util.Hashtable;
import org.more.submit.support.MapSubmitConfig;
/**
 * submit利用build模式创建{@link SubmitContext SubmitContext接口}的最后阶段，
 * CasingDirector类主要负责从{@link CasingBuild CasingBuild}中获取返回值然后创建SubmitContext接口对象。
 * 通过扩展CasingDirector类可以改变创建SubmitContext的方式从而扩展more的支撑外壳。
 * @version 2009-12-1
 * @author 赵永春 (zyc@byshell.org)
 */
public class CasingDirector {
    //========================================================================================Field
    private SubmitContext context = null; //组合之后生成的SubmitContext对象。
    private Config        config  = null;
    //==================================================================================Constructor
    /**
     * 创建一个submit框架外壳扩展生成器管理器。
     * @param config 该生成器在build时需要的配置对象。
     */
    public CasingDirector(Config config) {
        if (config == null)
            this.config = new MapSubmitConfig(new Hashtable<String, Object>(), null);
        else
            this.config = config;
    }
    //==========================================================================================Job
    /**
     * 调用生成器生成SubmitContext对象，生成的SubmitContext的对象可以需要通过getResult方法获取。
     * @param build 生成SubmitContext对象时需要用到的生成器。
     */
    public void build(CasingBuild build) throws Exception {
        build.init(this.config);//初始化生成器
        this.context = this.buildContext(build, config);
    }
    /**子类可以通过扩展该方法来决定具体SubmitContext的创建过程。此阶段build.init方法已经被调用。*/
    protected SubmitContext buildContext(CasingBuild build, Config config) {
        ActionContext context = build.getActionContext();
        if (context instanceof AbstractActionContext == true)
            //如果ActionContext类型是AbstractActionContext的子类则执行AbstractActionContext类型的初始化方法以启动AbstractActionContext类型的初始化过程。
            ((AbstractActionContext) context).init();
        return new ImplSubmitContext(context);
    }
    /**
     * 获取组合之后生成的SubmitContext对象。
     * @return 返回组合之后生成的SubmitContext对象。
     */
    public SubmitContext getResult() {
        return context;
    }
}