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
package org.more.submit.acs.guice;
import java.util.ArrayList;
import java.util.Collection;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.submit.acs.simple.AC_Simple;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
/**
 * 该类扩展了{@link AC_Simple}，首先判断是否是一个guice Bean，
 * 如果是则使用guice创建，否则使用{@link AC_Simple}方式创建。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class AC_Guice extends AC_Simple {
    private static Log log           = LogFactory.getLog(AC_Guice.class);
    public Injector    guiceInjector = null;
    public Object      modules       = null;
    //
    protected Object getBean(Class<?> type) throws Throwable {
        GBean annoBean = type.getAnnotation(GBean.class);
        if (annoBean == null)
            return super.getBean(type);
        if (guiceInjector == null) {
            ArrayList<Module> mods = new ArrayList<Module>();
            //解析modules并且配置Guice  Injector
            if (this.modules != null)
                if (this.modules.getClass().isArray() == true)
                    //1.数组形式
                    for (Object obj : (Object[]) this.modules)
                        this.add(mods, obj);
                else if (Collection.class.isAssignableFrom(this.modules.getClass()) == true)
                    //2.集合形式
                    for (Object obj : (Collection<?>) this.modules)
                        this.add(mods, obj);
                else
                    //3.其他
                    this.add(mods, this.modules);
            this.guiceInjector = Guice.createInjector(mods);
            log.info("AC for Guice Injector created.");
        }
        //调用Guice创建对象。
        return guiceInjector.getInstance(type);
    };
    private void add(ArrayList<Module> modules, Object module) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (module != null)
            modules.add(this.passModule(module));
    };
    private Module passModule(Object obj) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (obj == null)
            return null;
        if (obj instanceof Module)
            return (Module) obj;
        else
            return (Module) Thread.currentThread().getContextClassLoader().loadClass(obj.toString()).newInstance();
    };
};