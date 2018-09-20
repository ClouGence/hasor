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
package net.hasor.core.container;
import net.hasor.core.Provider;
import net.hasor.core.Scope;
import net.hasor.core.provider.SingleProvider;

import java.util.HashMap;
import java.util.Map;
/**
 * 一个自定义 Scope ，实现了线程间单例化
 * @version : 2015年11月9日
 * @author 赵永春 (zyc@hasor.net)
 */
public class MyScope implements Scope {
    private Map<Object, Provider<?>> singtonMap = new HashMap<Object, Provider<?>>();
    //
    public <T> Provider<T> scope(Object key, final Provider<T> provider) {
        Provider<?> returnData = singtonMap.get(key);
        if (returnData == null) {
            Provider<T> newSingleProvider = new SingleProvider<T>(provider);
            returnData = singtonMap.put(key, newSingleProvider);
            if (returnData == null) {
                returnData = newSingleProvider;
            }
        }
        return (Provider<T>) returnData;
    }
}