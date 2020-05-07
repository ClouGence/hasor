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
package net.hasor.dataql.fx.basic;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.utils.BooleanUtils;
import net.hasor.utils.NumberUtils;

import javax.inject.Singleton;

/**
 * 转换函数。函数库引入 <code>import 'net.hasor.dataql.fx.basic.ConvertUdfSource' as convert;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
public class ConvertUdfSource implements UdfSourceAssembly {
    /** 将对象转换为 Number */
    public static Number toInt(Object target) {
        if (target instanceof Number) {
            return (Number) target;
        } else if (target == null) {
            return 0;
        } else if (target instanceof String) {
            return NumberUtils.createNumber((String) target);
        }
        return 0;
    }

    /** 将对象转换为 String */
    public static String toString(Object target) {
        return String.valueOf(target);
    }

    /** 将对象转换为 Boolean */
    public static Boolean toBoolean(Object target) {
        if (target instanceof Boolean) {
            return (Boolean) target;
        } else if (target instanceof String) {
            return BooleanUtils.toBooleanObject((String) target);
        } else {
            return Boolean.FALSE;
        }
    }
}