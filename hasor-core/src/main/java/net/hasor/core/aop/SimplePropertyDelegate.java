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
package net.hasor.core.aop;
import java.util.Observable;

/**
 * PropertyDelegate 默认简单实现
 * @version : 2020-09-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class SimplePropertyDelegate extends Observable implements PropertyDelegate {
    private Object value = null;

    /**  */
    public SimplePropertyDelegate() {
        this(null);
    }

    /**  */
    public SimplePropertyDelegate(Object defaultValue) {
        this.value = defaultValue;
    }

    public Object getValue() {
        return get(this);
    }

    public void setValue(Object value) {
        set(this, value);
    }

    public Object get(Object target) {
        return this.value;
    }

    public void set(Object target, Object newValue) {
        if (this.value == newValue) {
            return;
        }
        try {
            this.value = newValue;
            this.setChanged();
            this.notifyObservers(newValue);
        } finally {
            this.clearChanged();
        }
    }
}
