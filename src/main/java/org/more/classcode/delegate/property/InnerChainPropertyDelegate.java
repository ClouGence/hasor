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
package org.more.classcode.delegate.property;
import org.more.classcode.AbstractClassConfig;
import org.more.classcode.MoreClassLoader;
/**
 * 
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerChainPropertyDelegate implements PropertyDelegate<Object> {
    private PropertyDelegate<Object> propertyDelegate = null;
    //
    public InnerChainPropertyDelegate(String className, String propertyName, ClassLoader loader) throws NoSuchFieldException {
        if (loader instanceof MoreClassLoader) {
            AbstractClassConfig cc = ((MoreClassLoader) loader).findClassConfig(className);
            if (cc != null && cc instanceof PropertyClassConfig) {
                PropertyClassConfig proCC = (PropertyClassConfig) cc;
                this.propertyDelegate = proCC.getPropertyDelegate(propertyName);
            }
        }
        //
        if (this.propertyDelegate == null) {
            throw new NoSuchFieldException("propertyDelegate is null.");
        }
    }
    //
    public Class<? extends Object> getType() {
        return this.propertyDelegate.getType();
    }
    public Object get(Object target) throws Throwable {
        return this.propertyDelegate.get(target);
    }
    public void set(Object target, Object newValue) throws Throwable {
        this.propertyDelegate.set(target, newValue);
    }
}