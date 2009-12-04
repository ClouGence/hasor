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
 * submit3.0框架外壳扩展生成器管理器，通过CasingBuild组合之后可以获得SubmitContext对象。
 * <br/>Date : 2009-12-1
 * @author 赵永春
 */
public class CasingDirector {
    //========================================================================================Field
    private SubmitContext manager = null; //组合之后生成的SubmitContext对象。
    private Config        config  = null;
    //==================================================================================Constructor
    /**
     * 创建一个submit2.0框架外壳扩展生成器管理器。
     * @param config 该生成器在buildManager时需要的配置对象。
     */
    public CasingDirector(Config config) {
        if (config == null)
            this.config = new MapSubmitConfig(new Hashtable<String, Object>(), null);
        this.config = config;
    }
    //==========================================================================================Job
    /**
     * 调用生成器生成SubmitContext对象，生成的SubmitContext的对象可以需要通过getResult方法获取。
     * @param build 生成SubmitContext对象时需要用到的生成器。
     */
    public void buildManager(CasingBuild build) {
        build.init(this.config);//初始化生成器
        this.manager = new SubmitContext(build.getActionFactory());
    }
    /**
     * 获取组合之后生成的SubmitContext对象。
     * @return 返回组合之后生成的SubmitContext对象。
     */
    public SubmitContext getResult() {
        return manager;
    }
}