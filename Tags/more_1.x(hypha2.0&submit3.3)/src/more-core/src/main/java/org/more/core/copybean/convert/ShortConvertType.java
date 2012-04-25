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
package org.more.core.copybean.convert;
import org.more.core.copybean.Convert;
import org.more.util.StringConvertUtil;
/**
 * CopyBean处理Short类型转换的辅助类。
 * @version 2009-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class ShortConvertType implements Convert<Short> {
    public boolean checkConvert(Class<?> toType) {
        return (toType == Short.class || toType == short.class) ? true : false;
    }
    public Short convert(Object object) {
        if (object == null)
            return 0;
        else if (object instanceof Short)
            return (Short) object;
        else
            return StringConvertUtil.parseShort(object.toString(), (short) 0);
    }
}