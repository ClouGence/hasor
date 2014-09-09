/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode.objects;
import org.more.classcode.PropertyDelegate;
import org.more.util.BeanUtils;
/**
 * 
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class SimplePropertyDelegate implements PropertyDelegate<Object> {
    private Class<?> type  = Object.class;
    private Object   value = null;
    //
    /***/
    public SimplePropertyDelegate(Object defaultValue) {
        if (defaultValue != null) {
            this.type = defaultValue.getClass();
        }
        this.value = defaultValue;
    }
    /***/
    public SimplePropertyDelegate(Class<?> targetType) {
        this.type = targetType;
        this.value = BeanUtils.getDefaultValue(targetType);
    }
    /***/
    public SimplePropertyDelegate(Class<?> targetType, Object defaultValue) {
        if (defaultValue == null) {
            defaultValue = BeanUtils.getDefaultValue(targetType);
        }
        this.type = targetType;
        this.value = defaultValue;
    }
    public Class<?> getType() {
        return this.type;
    }
    public Object get() throws Throwable {
        return this.value;
    }
    public void set(Object newValue) throws Throwable {
        this.value = newValue;
    }
}
