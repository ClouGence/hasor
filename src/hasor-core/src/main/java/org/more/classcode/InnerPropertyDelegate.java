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
package org.more.classcode;
import java.lang.reflect.InvocationTargetException;
/**
 * 
 * @version : 2014年9月9日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerPropertyDelegate implements PropertyDelegate<Object> {
    private String                   propertyName   = null;
    private boolean                  markRead       = true;
    private boolean                  markWrite      = true;
    private PropertyDelegate<Object> targetDelegate = null;
    //
    public InnerPropertyDelegate(String propertyName, PropertyDelegate<?> targetDelegate, boolean markRead, boolean markWrite) {
        this.propertyName = propertyName;
        this.markRead = markRead;
        this.markWrite = markWrite;
        this.targetDelegate = (PropertyDelegate<Object>) targetDelegate;
    }
    /**属性名*/
    public String propertyName() {
        return this.propertyName;
    }
    /**属性是否可读*/
    public boolean isMarkRead() {
        return markRead;
    }
    /**属性是否可写*/
    public boolean isMarkWrite() {
        return markWrite;
    }
    //
    public Class<?> getType() {
        return this.targetDelegate.getType();
    }
    public Object get() throws Throwable {
        try {
            return this.targetDelegate.get();
        } catch (Throwable e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else if (e instanceof InvocationTargetException) {
                throw ((InvocationTargetException) e).getTargetException();
            }
            throw e;
        }
    }
    public void set(Object newValue) throws Throwable {
        try {
            this.targetDelegate.set(newValue);
        } catch (Throwable e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else if (e instanceof InvocationTargetException) {
                throw ((InvocationTargetException) e).getTargetException();
            }
            throw e;
        }
    }
}