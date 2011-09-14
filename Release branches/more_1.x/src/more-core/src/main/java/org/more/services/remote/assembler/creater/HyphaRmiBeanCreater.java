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
package org.more.services.remote.assembler.creater;
import java.rmi.Remote;
import org.more.hypha.ApplicationContext;
import org.more.services.remote.assembler.AbstractRmiBeanCreater;
/**
 * Hypha代理
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class HyphaRmiBeanCreater extends AbstractRmiBeanCreater {
    private String             refBean            = null;
    private ApplicationContext applicationContext = null;
    //
    private Class<?>           refBeanType        = null;
    //
    public HyphaRmiBeanCreater(String refBean, ApplicationContext applicationContext) throws Throwable {
        this.refBean = refBean;
        this.applicationContext = applicationContext;
        this.refBeanType = this.applicationContext.getBeanType(refBean);
    };
    public Class<?>[] getFaces() throws Throwable {
        return this.getRemoteFaces(this.refBeanType.getInterfaces());
    };
    public Remote create() throws Throwable {
        //1.创建对象
        Object obj = this.applicationContext.getBean(this.refBean);
        //2.生成Remote代理
        return super.getRemoteByFaces(obj, this.getFaces());
    };
}