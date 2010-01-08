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
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.more.submit.Config;
/**
 * WebMoreBuilder类扩展了ClientMoreBuilder提供了web的支持，该类所使用的configFile参数是一个相对于站点的web相对路径。<br/>
 * 如果没有指定configFile参数，则configFile默认将表示为“/WEB-INF/more-config.xml”配置文件。
 * @version 2010-1-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebMoreBuilder extends ClientMoreBuilder implements Config {
    public static final String Default_ConfigXML = "/WEB-INF/more-config.xml";
    //==================================================================================Constructor
    /** WebMoreBuilder类扩展了ClientMoreBuilder提供了web的支持，该类将configFile参数所表示的路径从web相对路径转换为绝对路径。*/
    public WebMoreBuilder() throws IOException {
        super(false);
    }
    //==========================================================================================Abs
    @Override
    public void init(Config config) throws Exception {
        System.out.println("init WebMoreBuilder...");
        if (this.config == null)
            super.init(this.getWebMoreBuilderConfig(config));
        System.out.println("init WebMoreBuilder OK");
    }
    //=========================================================================================Impl
    private Config propxyConfig;
    protected Config getWebMoreBuilderConfig(Config config) {
        this.propxyConfig = config;
        return this;
    }
    @Override
    public Object getInitParameter(String name) {
        if ("configFile".equals(name) == true) {
            ServletContext sc = (ServletContext) this.config.getContext();
            Object obj = this.propxyConfig.getInitParameter(name);
            if (obj == null)
                return sc.getRealPath(WebMoreBuilder.Default_ConfigXML);//使用默认配置。
            else
                return sc.getRealPath(obj.toString());//使用configFile配置参数
        } else
            return this.propxyConfig.getInitParameter(name);
    }
    @Override
    public Object getContext() {
        return this.propxyConfig.getContext();
    }
    @Override
    public Enumeration<String> getInitParameterNames() {
        return this.propxyConfig.getInitParameterNames();
    }
}