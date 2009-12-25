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
package org.more.submit.casing.more;
import javax.servlet.ServletContext;
import org.more.InvokeException;
import org.more.beans.core.ResourceBeanFactory;
import org.more.beans.resource.XmlFileResource;
import org.more.submit.Config;
/**
 * WebMoreBuilder类扩展了ClientMoreBuilder提供了web的支持，init方法传入configFile参数WebMoreBuilder类会自动使用
 * ServletContext对象转换其路径然后创建ResourceBeanFactory以完成初始化。
 * <br/>Date : 2009-12-2
 * @author 赵永春
 */
public class WebMoreBuilder extends ClientMoreBuilder {
    //==================================================================================Constructor
    /** 使用Web形式构建More的ActionManager，如果在构造方法中没有指定任何参数则当ActionManager在初始化时候回用过init方法获取，SpringContext */
    public WebMoreBuilder() {}
    //==========================================================================================Job
    @Override
    public void init(Config config) throws InvokeException {
        this.config = config;
        System.out.println("init WebMoreBuilder...");
        ServletContext sc = (ServletContext) this.getConfig().getContext();
        String configFile = sc.getRealPath(config.getInitParameter("configFile"));
        this.factory = new ResourceBeanFactory(new XmlFileResource(configFile), null); //创建对象
        System.out.println("init WebMoreBuilder OK");
    }
}