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
package org.more.hypha.beans.assembler.parser;
import org.more.hypha.ApplicationContext;
import org.more.hypha.NoDefineBeanException;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.define.Relation_ValueMetaData;
import org.more.hypha.commons.logic.ValueMetaDataParser;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 引用类型bean解析。
 * @version 2011-2-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class RelationBean_MetaData_Parser implements ValueMetaDataParser<Relation_ValueMetaData> {
    private static ILog log = LogFactory.getLog(RelationBean_MetaData_Parser.class);
    /*------------------------------------------------------------------------------*/
    public Object parser(Object targetObject, Relation_ValueMetaData data, ValueMetaDataParser<ValueMetaData> rootParser, ApplicationContext context) throws Throwable {
        String refBeanID = data.getRefBean();
        String refPackage = data.getRefPackage();
        if (context.containsBean(refBeanID) == false) {
            log.warning("ref bean {%0} bean is not exist", refBeanID);
            refBeanID = refPackage + "." + refBeanID;
            if (context.containsBean(refBeanID) == false) {
                log.error("ref bean {%0} is not exist", refBeanID);
                throw new NoDefineBeanException("ref bean " + refBeanID + " is not exist");
            }
        }
        //
        Object res = null;
        if (context instanceof AbstractApplicationContext == true) {
            //如果是AbstractApplicationContext类型还可以取得getBean的参数进行传递。
            AbstractApplicationContext aapp = (AbstractApplicationContext) context;
            Object[] params = aapp.getGetBeanParams();
            res = aapp.getBean(refBeanID, params);
        } else
            res = context.getBean(refBeanID);
        return res;
    }
};