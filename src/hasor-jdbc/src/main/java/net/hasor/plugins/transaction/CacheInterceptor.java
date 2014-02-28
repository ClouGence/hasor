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
package net.hasor.plugins.transaction;
import java.lang.reflect.Method;
import java.util.List;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.Hasor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.more.json.JSON;
/**
 * 拦截器
 * @version : 2013-11-8
 * @author 赵永春(zyc@hasor.net)
 */
class CacheInterceptor implements MethodInterceptor, AppContextAware {
    private AppContext appContext = null;
    public CacheInterceptor(ApiBinder apiBinder) {
        /* 注册 AppContextAware 接口，以便获取到 AppContext 接口类型。*/
        apiBinder.registerAware(this);
    }
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    //
    public Object invoke(MethodInvocation invocation) throws Throwable {
        /*1.准备必要的参数*/
        Method targetMethod = invocation.getMethod();
        Transaction cacheAnno = targetMethod.getAnnotation(Transaction.class);/*方法上的NeedCache*/
        if (cacheAnno == null)
            cacheAnno = targetMethod.getDeclaringClass().getAnnotation(Transaction.class);/*方法所处类上的NeedCache*/
        if (cacheAnno == null)
            return invocation.proceed();
        List<CacheCreator> creatorList = appContext.findBindingBean(CacheCreator.class);/*查找 CacheCreator 实现类*/
        if (creatorList == null || creatorList.isEmpty()) {
            Hasor.logWarn("does not define the CacheCreator.");
            return invocation.proceed();
        }
        //2.获取缓存
        CacheCreator cacheCreator = creatorList.get(0);
        Cache cache = cacheCreator.getCacheByName(appContext, cacheAnno.groupName());
        if (cache == null) {
            Hasor.logWarn("Cache %s is not Defile. at Method %s.", cacheAnno.groupName(), targetMethod);
            return invocation.proceed();
        }
        //3.获取Key
        StringBuilder cacheKey = new StringBuilder(targetMethod.toString());
        Object[] args = invocation.getArguments();
        if (args != null)
            for (Object arg : args) {
                if (arg == null) {
                    cacheKey.append("NULL");
                    continue;
                }
                /*保证arg参数不为空*/
                cacheKey.append(JSON.toString(arg));
            }
        Hasor.logDebug("MethodInterceptor Method : %s", targetMethod);
        Hasor.logDebug("MethodInterceptor Cache key :%s", cacheKey.toString());
        //4.操作缓存
        String key = cacheKey.toString();
        Object returnData = null;
        if (cache.hasCache(key) == true) {
            Hasor.logDebug("the method return data is from Cache.");
            returnData = cache.fromCache(key);
        } else {
            Hasor.logDebug("set data to Cache key :" + key);
            returnData = invocation.proceed();
            cache.toCache(key, returnData);
        }
        return returnData;
    }
}