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
package net.hasor.plugins.aware;
import net.hasor.core.AppContext;
import org.more.ref.WeakArrayList;
import org.more.util.ContextClassLoaderLocal;
/**
 * 
 * @version : 2013-11-8
 * @author 赵永春(zyc@hasor.net)
 */
public interface AppContextAware {
    public void setAppContext(AppContext appContext);
    //
    //
    //
    //
    /**用于注册 AppContextAware 的工具类。*/
    public static class AwareUtil {
        /*若引用的方式保存AppContextAware接口对象*/
        private static ContextClassLoaderLocal<WeakArrayList<AppContextAware>> awareList = new ContextClassLoaderLocal<WeakArrayList<AppContextAware>>();
        /**注册一个 AppContextAware 接口对象*/
        public synchronized static void registerAppContextAware(AppContextAware aware) {
            WeakArrayList<AppContextAware> weakList = awareList.get();
            if (weakList == null) {
                weakList = new WeakArrayList<AppContextAware>();
                awareList.set(weakList);
            }
            weakList.add(aware);
        }
        /**执行通知*/
        protected synchronized void doAware(AppContext appContext) {
            WeakArrayList<AppContextAware> weakList = awareList.get();
            if (weakList == null)
                return;
            for (AppContextAware weak : weakList)
                weak.setAppContext(appContext);
        }
    }
}