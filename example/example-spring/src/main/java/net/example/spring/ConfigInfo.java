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
package net.example.spring;
import net.hasor.core.InjectSettings;
/**
 *
 * @version : 2016年2月15日
 * @author 赵永春(zyc@hasor.net)
 */
public class ConfigInfo {
    // .由 hasor-config.xml 配置
    @InjectSettings("myapp.config")
    private String hasorConfig;
    // .由 spring.xml 中通过 propertyConfigurer 方式配置（将Spring中加载的属性共享给Hasor）
    @InjectSettings("${prop-config}")
    private String propConfig;
    // .由 spring.xml 中通过 h:property 方式配置
    @InjectSettings("${spring-config}")
    private String springConfig;
    //
    //
    //
    public String getSpring() {
        return this.springConfig;
    }
    public String getHasor() {
        return this.hasorConfig;
    }
    public String getProp() {
        return this.propConfig;
    }
    //
    public String toString() {
        return "HasorBean - [spring:" + this.springConfig //
                + ", hasor =" + this.hasorConfig  //
                + ", prop =" + this.propConfig + "]";
    }
}