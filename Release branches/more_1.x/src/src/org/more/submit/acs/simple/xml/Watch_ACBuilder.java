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
package org.more.submit.acs.simple.xml;
import java.lang.annotation.Annotation;
import org.more.hypha.anno.KeepWatchParser;
import org.more.hypha.anno.define.Bean;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.submit.ActionContextBuilder;
import org.more.submit.acs.simple.propxy_acb.ACBuilder_ForAnno;
import org.more.submit.acs.simple.propxy_acb.ACBuilder_ForBean;
/**
 * 该类的目的是用于解析ACBuilder注解，该注解可以和Bean注解配合使用，达到不同的效果。
 * @version 2010-10-14
 * @author 赵永春 (zyc@byshell.org)
 */
class Watch_ACBuilder implements KeepWatchParser {
    private B_Config config = null;
    public Watch_ACBuilder(B_Config config) {
        this.config = config;
    }
    public void process(Object target, Annotation annoData, XmlDefineResource resource) {
        Class<?> type = (Class<?>) target;
        //
        ActionContextBuilder acBuilder = null;
        Bean beanMark = type.getAnnotation(Bean.class);
        if (beanMark != null)
            acBuilder = new ACBuilder_ForBean(beanMark, type);
        else
            acBuilder = new ACBuilder_ForAnno(type);
        if (acBuilder != null)
            this.config.build.addActionContexBuilder(acBuilder);
    };
};