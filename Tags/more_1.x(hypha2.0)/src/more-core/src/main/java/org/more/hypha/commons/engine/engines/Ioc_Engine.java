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
package org.more.hypha.commons.engine.engines;
import java.util.Collection;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.BeanPropertyDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.commons.logic.IocEngine;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.util.BeanUtil;
/**
 * 使用传统方式进行ioc。
 * @version : 2011-6-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class Ioc_Engine extends IocEngine {
    private static Log log = LogFactory.getLog(Ioc_Engine.class);
    public void ioc(Object target, AbstractBeanDefine define, Object[] params) throws Throwable {
        Collection<? extends BeanPropertyDefine> bpds = define.getPropertys();
        if (bpds == null) {
            log.debug("Propertys is null so ioc not execute!");
            return;
        }
        ValueMetaDataParser<ValueMetaData> root = this.getRootParser();
        int index = 0;
        int size = bpds.size();
        for (BeanPropertyDefine bpd : bpds) {
            String name = bpd.getName();
            Object value = root.parser(target, bpd.getMetaData(), this.getRootParser(), this.getApplicationContext());
            log.debug("writeProperty {%0} of {%1} , name is '{%2}'", index, size, name);
            BeanUtil.writePropertyOrField(target, name, value);
            index++;
        }
        log.debug("ioc finish!");
    }
}