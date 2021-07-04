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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @version : 2021年07月04日
 * @author 赵永春 (zyc@hasor.net)
 */
public class GenerateBeanID {
    private final Map<String, AtomicInteger> integerMap = new HashMap<>();

    public String generateBeanID(Class<?> bindingType) {
        String typeName = bindingType.getName();
        if (!integerMap.containsKey(typeName)) {
            integerMap.computeIfAbsent(typeName, s -> new AtomicInteger());
        } else {
            AtomicInteger integer = integerMap.get(typeName);
            typeName = typeName + "#" + integer.incrementAndGet();
        }
        return typeName;
    }
}
