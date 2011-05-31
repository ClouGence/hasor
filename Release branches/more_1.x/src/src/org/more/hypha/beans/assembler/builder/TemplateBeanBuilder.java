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
package org.more.hypha.beans.assembler.builder;
import org.more.core.error.SupportException;
import org.more.hypha.beans.define.TemplateBeanDefine;
import org.more.hypha.commons.engine.AbstractBeanBuilder;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * TemplateBeanDefine类型bean解析，该类型bean啥也不支持。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class TemplateBeanBuilder extends AbstractBeanBuilder<TemplateBeanDefine> {
    private static ILog log = LogFactory.getLog(TemplateBeanBuilder.class);
    /*------------------------------------------------------------------------------*/
    public Class<?> loadType(TemplateBeanDefine define, Object[] params) {
        String defineID = define.getID();
        log.error("TemplateBean {%0} doesn`t Support this method.", defineID);
        throw new SupportException("TemplateBean " + defineID + " doesn`t Support this method.");
    }
    public <O> O createBean(TemplateBeanDefine define, Object[] params) {
        String defineID = define.getID();
        log.error("TemplateBean {%0} doesn`t Support this method.", defineID);
        throw new SupportException("TemplateBean " + defineID + " doesn`t Support this method.");
    }
};