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
package org.more.submit.acs.simple.propxy_acb;
import org.more.hypha.ApplicationContext;
import org.more.hypha.anno.assembler.AnnoMetaDataUtil;
import org.more.hypha.anno.define.Bean;
import org.more.submit.AbstractACBuilder;
import org.more.submit.ActionContextBuilder;
/**
 * 该类是表示一个由注解标记为ACBuilder但是同时又被标记成为Bean。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ACBuilder_ForBean extends AbstractACBuilder {
    private Bean     annoBean      = null;
    private Class<?> acBuilderType = null;
    public ACBuilder_ForBean(Bean annoBean, Class<?> acBuilderType) {
        this.annoBean = annoBean;
        this.acBuilderType = acBuilderType;
    };
    protected ActionContextBuilder createBuilder() throws Throwable {
        ApplicationContext app = (ApplicationContext) this.getConfig().getContext();
        return app.getBean(AnnoMetaDataUtil.getBeanID(annoBean, acBuilderType));
    };
};