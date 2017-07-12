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
package net.test.hasor.core._02_ioc.example;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;
import net.test.hasor.core._01_bean.pojo.PojoBean;
/**
 * 注解方式注入Bean。
 * @version : 2014-1-3
 * @author 赵永春(zyc@hasor.net)
 */
public class AnnoIocBean {
    @Inject
    private PojoBean iocBean      = null; // <- 自动创建 PojoBean 对象并注入进来。
    @Inject
    private PojoBean iocBeanField = null; // <- 自动创建 PojoBean 对象并注入进来。
    @InjectSettings("myself.myname")
    private String   myName       = null; // <- 自动创建 PojoBean 对象并注入进来。
    @InjectSettings("envName")
    private String   envName      = null; // <- 自动创建 PojoBean 对象并注入进来。
    //
    public PojoBean getIocBean() {
        return iocBean;
    }
    public PojoBean getIocBeanField() {
        return iocBeanField;
    }
    public void setIocBean(PojoBean iocBean) {
        this.iocBean = iocBean;
    }
    public void setIocBeanField(PojoBean iocBeanField) {
        this.iocBeanField = iocBeanField;
    }
    public String getMyName() {
        return myName;
    }
    public void setMyName(String myName) {
        this.myName = myName;
    }
    public String getEnvName() {
        return envName;
    }
    public void setEnvName(String envName) {
        this.envName = envName;
    }
}