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
import javax.servlet.ServletContext;
import org.more.InvokeException;
import org.more.submit.ActionFactory;
import org.more.submit.CasingBuild;
import org.more.submit.Config;
import org.more.submit.FilterFactory;
import org.springframework.context.support.AbstractApplicationContext;
/**
 * 当配置了Spring的listener时候需要使用WebSpringCasingBuilder进行集成spring到submit中。
 * 注意WebSpringCasingBuilder的启动顺序一定要在spring的listener之后启动。否则More将获取不到SpringContext。
 * Date : 2009-6-30
 * @author 赵永春
 */
public class WebSpringCasingBuilder extends CasingBuild {
    private SpringActionFactory        saf           = null;
    private SpringFilterFactory        sff           = null;
    private AbstractApplicationContext springContext = null;
    /** 使用Web形式构建More的ActionManager，如果在构造方法中没有指定任何参数则当ActionManager在初始化时候回用过init方法获取，SpringContext */
    public WebSpringCasingBuilder() {}
    /**
     * 该构造方法可以使开发人员通过指定ServletContext来获取SpringContext。如果在该构造方法中获取到了SpringContext则在init方法时将忽略获取
     * SpringContext的过程。如果该方法中没有获取到SpringContext则回引发InvokeException异常
     * @param sc 传入的ServletContext对象。
     * @throws InvokeException 如果没有获取到SpringContext时候则会引发该异常。
     */
    public WebSpringCasingBuilder(ServletContext sc) throws InvokeException {
        //获取spring上下文,该方法会确保获取到对象，如果获取不到或者类型错误将会引发异常。
        this.springContext = this.getSpringContext(sc);
    }
    @Override
    public void init(Config config) throws InvokeException {
        super.init(config);
        if (this.springContext == null) {
            ServletContext sc = (ServletContext) this.getConfig().getContext();
            this.springContext = this.getSpringContext(sc);//该方法会确保获取到对象，如果获取不到或者类型错误将会引发异常。
        }
        //创建对象
        saf = new SpringActionFactory((AbstractApplicationContext) springContext);//Spring的action工厂
        sff = new SpringFilterFactory((AbstractApplicationContext) springContext);//Spring的filter工厂
    }
    /** 该方法负责从ServletContext中获取Spring工厂对象。 */
    private AbstractApplicationContext getSpringContext(ServletContext sc) {
        //获取spring上下文
        Object context = sc.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
        if (context == null || context instanceof AbstractApplicationContext == false)
            throw new InvokeException("无法读取org.springframework.web.context.WebApplicationContext.ROOT属性对象，该属性对象或者为空或者没有继承spring的AbstractApplicationContext抽象类");
        return (AbstractApplicationContext) context;
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