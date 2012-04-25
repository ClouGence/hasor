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
package org.more.services.submit.xml.propxy_acb;
import org.more.hypha.ApplicationContext;
import org.more.services.submit.AbstractACBuilder;
import org.more.services.submit.ActionContextBuilder;
/**
 * 该类是表示一个由xml配置文件配置的{@link ActionContextBuilder}生成器，但是该类属于一个代理类。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ACBuilder_ForXml extends AbstractACBuilder {
    private String refBean = null;
    //
    public ACBuilder_ForXml(String refBean) {
        this.refBean = refBean;
    };
    protected ActionContextBuilder createBuilder() throws Throwable {
        ApplicationContext context = (ApplicationContext) this.getConfig().getContext();
        return context.getBean(this.refBean);
    };
};