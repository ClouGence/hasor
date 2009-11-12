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
package org.more.beans.core.injection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.more.beans.BeanFactory;
import org.more.beans.info.BeanProperty;
/**
 * 集合类型属性解析器。
 * Date : 2009-11-8
 * @author 赵永春
 */
public class TypeParser {
    public static Object passerArray(Object object, Object[] getBeanParam, BeanProperty prop, BeanFactory context) {
        return null;
    }
    public static List<?> passerList(Object object, Object[] getBeanParam, BeanProperty prop, BeanFactory context) {
        return null;
    }
    public static Map<?, ?> passerMap(Object object, Object[] getBeanParam, BeanProperty prop, BeanFactory context) {
        return null;
    }
    public static Set<?> passerSet(Object object, Object[] getBeanParam, BeanProperty prop, BeanFactory context) {
        return null;
    }
    /**解析一个属性类型为对象*/
    public static Object passerType(Object object, Object[] getBeanParam, BeanProperty prop, BeanFactory context) {
        return null;
    }
}
