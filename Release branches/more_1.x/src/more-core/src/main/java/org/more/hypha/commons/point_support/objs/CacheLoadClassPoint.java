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
package org.more.hypha.commons.point_support.objs;
import java.util.HashMap;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.ApplicationContext;
import org.more.hypha.commons.logic.LoadClassPoint;
import org.more.hypha.point.PointChain;
/**
 * 该扩展点的作用是缓存已经装载的类型
 * @version : 2011-6-28
 * @author 赵永春 (zyc@byshell.org)
 */
public class CacheLoadClassPoint implements LoadClassPoint {
    private static Log                log             = LogFactory.getLog(CacheLoadClassPoint.class);
    private HashMap<String, Class<?>> cacheDefineType = new HashMap<String, Class<?>>();
    public Object doFilter(ApplicationContext applicationContext, Object[] params, PointChain chain) throws Throwable {
        //1.合法性判断
        if (params == null || params.length == 0)
            return chain.doChain(applicationContext, params);
        if (params[0] instanceof AbstractBeanDefine == false)
            return chain.doChain(applicationContext, params);
        //2.执行缓存
        AbstractBeanDefine define = (AbstractBeanDefine) params[0];
        String id = define.getID();
        if (this.cacheDefineType.containsKey(id) == true) {
            log.debug("load Define {%0} Type form Cache!", id);
            return this.cacheDefineType.get(id);
        }
        log.debug("load Define {%0} Type By New!", id);
        Class<?> type = (Class<?>) chain.doChain(applicationContext, params);
        this.cacheDefineType.put(id, type);
        return type;
    }
}