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
import org.more.core.log.ILog;
import org.more.core.log.LogFactory;
import org.more.hypha.beans.define.VariableBeanDefine;
import org.more.hypha.beans.define.VariableBeanDefine.VariableType;
import org.more.hypha.commons.logic.AbstractBeanBuilder;
import org.more.util.StringConvertUtil;
/**
 * 变量类型bean，该类型bean不能支持aop。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class VariableBeanBuilder extends AbstractBeanBuilder<VariableBeanDefine> {
    private static ILog log = LogFactory.getLog(VariableBeanBuilder.class);
    /*------------------------------------------------------------------------------*/
    public Class<?> loadType(VariableBeanDefine define, Object[] params) {
        VariableType vt = define.getType();
        Class<?> vtClass = VariableBeanDefine.getType(vt);
        log.debug("Variable Bean type = {%0}", vtClass);
        return vtClass;
    }
    public Object loadBean(VariableBeanDefine define, Object[] params) throws Throwable {
        String vValye = define.getValue();
        Class<?> classType = this.getApplicationContext().getBeanType(define.getID(), params);
        Object value = StringConvertUtil.changeType(vValye, classType);
        log.debug("Variable Bean value = {%0}", value);
        return value;
    }
};