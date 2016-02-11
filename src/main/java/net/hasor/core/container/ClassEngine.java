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
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.core.info.AopBindInfoAdapter;
import org.more.classcode.aop.AopClassConfig;
import org.more.classcode.aop.AopMatcher;
/**
 * 负责根据Class或BindInfo创建BeanType。
 *  ** 不支持热部署，该类会造成类型无法被回收。
 * @version : 2015年6月26日
 * @author 赵永春(zyc@hasor.net)
 */
class ClassEngine {
    private static ConcurrentHashMap<Class<?>, AopClassConfig> buildEngineMap = new ConcurrentHashMap<Class<?>, AopClassConfig>();
    public static Class<?> buildType(Class<?> targetType, ClassLoader rootLosder, List<AopBindInfoAdapter> aopList) throws ClassNotFoundException, IOException {
        if (AopClassConfig.isSupport(targetType) == false) {
            return targetType;
        }
        //
        Class<?> newType = targetType;
        AopClassConfig engine = buildEngineMap.get(targetType);
        if (engine == null) {
            engine = new AopClassConfig(targetType, rootLosder);
            for (AopBindInfoAdapter aop : aopList) {
                if (aop.getMatcherClass().matches(targetType) == false) {
                    continue;
                }
                AopMatcher aopMatcher = new ClassAopMatcher(aop.getMatcherMethod());
                engine.addAopInterceptor(aopMatcher, aop);
            }
            engine = buildEngineMap.putIfAbsent(targetType, engine);
            if (engine == null) {
                engine = buildEngineMap.get(targetType);
            }
        }
        if (engine.hasChange() == true) {
            newType = engine.toClass();
        } else {
            newType = engine.getSuperClass();
        }
        return newType;
    }
}