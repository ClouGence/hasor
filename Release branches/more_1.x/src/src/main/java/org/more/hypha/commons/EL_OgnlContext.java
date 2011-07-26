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
package org.more.hypha.commons;
import java.util.Map;
import org.more.core.ognl.OgnlContext;
import org.more.hypha.ELObject;
import org.more.hypha.EvalExpression;
import org.more.util.attribute.IAttribute;
/**
 * {@link EvalExpression}接口的实现类，在该类上可以自由使用{@link IAttribute}接口而不用考虑是否会影响到整体。
 * Date : 2011-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
class EL_OgnlContext extends OgnlContext {
    public EL_OgnlContext(Map<String, Object> map) {
        super(map);
    }
    public Object get(Object key) {
        Object obj = super.get(key);
        if (obj != null)
            if (obj instanceof ELObject)
                return ((ELObject) obj).getValue();
        return obj;
    }
    public Object put(Object key, Object value) {
        Object obj = super.get(key);
        if (obj != null)
            if (obj instanceof ELObject) {
                ((ELObject) obj).setValue(value);
                return value;
            }
        return super.put(key, value);
    }
};