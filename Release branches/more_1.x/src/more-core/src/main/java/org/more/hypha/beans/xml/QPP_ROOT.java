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
package org.more.hypha.beans.xml;
import java.util.ArrayList;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractPropertyDefine;
import org.more.hypha.ValueMetaData;
import org.more.hypha.beans.define.PropertyType;
import org.more.hypha.beans.define.Simple_ValueMetaData;
import org.more.util.attribute.IAttribute;
/**
 * 属性快速解析器，将字符串值解析为指定的类型。
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class QPP_ROOT implements QPP {
    private static Log     log        = LogFactory.getLog(QPP_ROOT.class);
    private ArrayList<QPP> parserList = new ArrayList<QPP>();             //属性快速解析器定义
    /**注册一个快速属性值解析器。*/
    public synchronized void regeditTypeParser(QPP parser) {
        if (parser == null) {
            log.warning("regedit TypeParser error , parser is null.");
            return;
        }
        if (this.parserList.contains(parser) == false) {
            log.debug("{%0} parser regedit OK!", parser);
            this.parserList.add(parser);
        } else
            log.error("{%0} parser is exist.", parser);
    };
    /**取消一个快速属性值解析器的注册。*/
    public synchronized void unRegeditTypeParser(QPP parser) {
        if (parser == null) {
            log.warning("unRegedit TypeParser error , parser is null.");
            return;
        }
        if (this.parserList.contains(parser) == true) {
            log.debug("{%0} parser unRegedit OK!", parser);
            this.parserList.remove(parser);
        } else
            log.error("unRegedit error {%0} is not exist.", parser);
    };
    /**将属性值解析为某一特定类型的值，将value表述的值转换成指定的元信息描述。*/
    public synchronized ValueMetaData parser(IAttribute<String> att, AbstractPropertyDefine property) {
        ValueMetaData valueMETADATA = null;
        for (QPP tp : parserList) {
            valueMETADATA = tp.parser(att, property);
            if (valueMETADATA != null)
                return valueMETADATA;
        }
        log.debug("parser use null.");
        /**使用默认值null*/
        Simple_ValueMetaData simple = new Simple_ValueMetaData();
        simple.setValueMetaType(PropertyType.Null);
        simple.setValue(null);
        return simple;
    };
}