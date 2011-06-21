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
package org.more.core.copybean.type;
import org.more.core.copybean.ConvertType;
import org.more.util.StringConvertUtil;
/**
 * CopyBean处理Short类型转换的辅助类。
 * @version 2009-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class ShortConvertType extends ConvertType {
    /**  */
    private static final long serialVersionUID = -3319752731163417948L;
    public boolean checkType(Object from, Class<?> to) {
        return (to == Short.class || to == short.class) ? true : false;
    }
    public Object convert(Object object) {
        if (object == null)
            return 0;
        else
            return StringConvertUtil.parseShort(object.toString(), (short) 0);
    }
}