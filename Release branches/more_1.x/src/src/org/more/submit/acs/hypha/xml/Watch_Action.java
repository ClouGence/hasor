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
package org.more.submit.acs.hypha.xml;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.more.hypha.anno.KeepWatchParser;
import org.more.hypha.anno.define.Bean;
import org.more.hypha.beans.assembler.MetaDataUtil;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.submit.acs.hypha.ActionPack;
import org.more.submit.acs.hypha.Action;
/**
 * 该类检测Action注解使用。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
class Watch_Action implements KeepWatchParser {
    private B_Config config = null;
    public Watch_Action(B_Config config) {
        this.config = config;
    }
    public void process(Object target, Annotation annoData, XmlDefineResource resource) {
        Method method = (Method) target;
        Class<?> type = method.getDeclaringClass();
        Bean beanAnnoData = type.getAnnotation(Bean.class);
        //1.生称标记路径。
        StringBuffer mStr = new StringBuffer();
        StringBuffer _mStr = new StringBuffer();
        if (beanAnnoData != null)
            mStr.append(MetaDataUtil.getBeanID(beanAnnoData, type));
        else
            mStr.append(type.getName());
        mStr.append(".");
        mStr.append(method.getName());
        _mStr.append(mStr.toString());
        mStr.append("(");
        for (Class<?> param : method.getParameterTypes())
            mStr.append(param.getName() + ",");
        int length = mStr.length();
        if (mStr.charAt(length - 1) == ',')
            mStr.deleteCharAt(length - 1);
        mStr.append(")");
        //2.注册Mapping
        Action ac = (Action) annoData;
        String value = ac.value();
        if (value.equals("") == true)
            value = _mStr.toString();
        B_AnnoActionInfo mapping = new B_AnnoActionInfo();
        //3.确定是否定义了包
        ActionPack pack = method.getAnnotation(ActionPack.class);
        if (pack == null)
            pack = type.getAnnotation(ActionPack.class);
        if (pack != null)
            mapping.packageString = pack.value();
        mapping.actionPath = mStr.toString();
        mapping.mappingPath = value;
        this.config.acMappingList.add(mapping);
    };
};