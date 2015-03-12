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
package net.hasor.web.jstl.tagfun;
import net.hasor.core.AppContext;
import net.hasor.core.Provider;
import net.hasor.web.startup.RuntimeListener;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-12-24
 * @author 赵永春(zyc@hasor.net)
 */
public class Functions {
    protected static AppContext getAppContext() {
        AppContext appContext = RuntimeListener.getLocalAppContext();
        if (appContext != null) {
            return appContext;
        }
        throw new NullPointerException("AppContext is undefined.");
    }
    //
    public static Object defineBean(final String defineBean) {
        if (StringUtils.isBlank(defineBean)) {
            return null;
        }
        return Functions.getAppContext().getInstance(defineBean);
    }
    public static Object defineType(final String className) throws ClassNotFoundException {
        Class<?> defineType = Class.forName(className);
        return Functions.getAppContext().getInstance(defineType);
    }
    public static Object defineBind(final String name, final String bindingType) throws ClassNotFoundException {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (StringUtils.isBlank(bindingType)) {
            return null;
        }
        Class<?> defineType = Class.forName(bindingType);
        //
        Provider<?> provider = Functions.getAppContext().findBindingProvider(name, defineType);
        if (provider != null) {
            return provider.get();
        }
        return null;
    }
    public static boolean hasBean(final String defineBean) {
        if (StringUtils.isBlank(defineBean)) {
            return false;
        }
        return Functions.getAppContext().containsBindID(defineBean);
    }
    public static boolean hasBind(final String name, final String className) throws ClassNotFoundException {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        Class<?> defineType = Class.forName(className);
        //
        Provider<?> provider = Functions.getAppContext().findBindingProvider(name, defineType);
        return provider != null;
    }
}