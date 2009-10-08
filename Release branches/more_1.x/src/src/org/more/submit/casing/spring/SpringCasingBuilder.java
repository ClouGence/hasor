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
package org.more.submit.casing.spring;
import org.more.InvokeException;
import org.more.submit.ActionFactory;
import org.more.submit.CasingBuild;
import org.more.submit.Config;
import org.more.submit.FilterFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
/**
 * 当手动启动了Spring之后需要将spring与submit集成需要使用SpringCasingBuilder进行集成。
 * 注意SpringCasingBuilder在初始化时候需要spring的AbstractApplicationContext类型对象
 * 一般情况下，spring的context对象都继承自AbstractApplicationContext对象。
 * Date : 2009-6-30
 * @author 赵永春
 */
public class SpringCasingBuilder extends CasingBuild {
    private SpringActionFactory        saf           = null;
    private SpringFilterFactory        sff           = null;
    private AbstractApplicationContext springContext = null;
    /**
     * 通过一个SpringContext对象创建SpringCasingBuilder。
     * @param springContext SpringContext对象
     */
    public SpringCasingBuilder(AbstractApplicationContext springContext) {
        if (springContext == null)
            throw new NullPointerException("springContext参数不能为空。");
        this.springContext = springContext;
    }
    /**
     * 通过一个SpringContext对象创建SpringCasingBuilder。
     * @param configLocation 配置文件位置该配置文件位置是根据ClassPath
     */
    public SpringCasingBuilder(String configLocation) {
        this.springContext = new FileSystemXmlApplicationContext(configLocation);
        if (this.springContext == null)
            throw new InvokeException("无法创建FileSystemXmlApplicationContext对象，请检查配置文件位置");
    }
    @Override
    public void init(Config config) throws InvokeException {
        super.init(config);
        saf = new SpringActionFactory((AbstractApplicationContext) springContext);//Spring的action工厂
        sff = new SpringFilterFactory((AbstractApplicationContext) springContext);//Spring的filter工厂
    }
    @Override
    public ActionFactory getActionFactory() {
        return this.saf;
    }
    @Override
    public FilterFactory getFilterFactory() {
        return this.sff;
    }
}