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
package net.hasor.core.info;
import net.hasor.core.PropertyDelegate;
import net.hasor.core.Provider;
import net.hasor.core.aop.ReadWriteType;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @version : 2020-09-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class DelegateBindInfoAdapter implements Supplier<PropertyDelegate>, PropertyDelegate {
    private final Predicate<Class<?>>                  matcherClass;
    private final String                               propertyName;
    private final Class<?>                             propertyType;
    private final Supplier<? extends PropertyDelegate> propertyDelegate;
    private final ReadWriteType                        rwType;

    public DelegateBindInfoAdapter(Predicate<Class<?>> matcherClass, String propertyName, Class<?> propertyType, Supplier<? extends PropertyDelegate> propertyDelegate, ReadWriteType rwType) {
        this.matcherClass = matcherClass;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.propertyDelegate = Provider.of(propertyDelegate).asSingle();
        this.rwType = rwType;
    }

    public Predicate<Class<?>> getMatcherClass() {
        return this.matcherClass;
    }

    public String getName() {
        return this.propertyName;
    }

    public Class<?> getType() {
        return this.propertyType;
    }

    public ReadWriteType getRwType() {
        return this.rwType;
    }

    @Override
    public PropertyDelegate get() {
        return this;
    }

    @Override
    public Object get(Object target) throws Throwable {
        return this.propertyDelegate.get().get(target);
    }

    @Override
    public void set(Object target, Object newValue) throws Throwable {
        this.propertyDelegate.get().set(target, newValue);
    }
}