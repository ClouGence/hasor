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
package org.more.webserver;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.binder.ApiBinder;
import org.platform.context.AbstractModuleListener;
import org.platform.context.AppContext;
import org.platform.context.InitListener;
import org.platform.context.setting.Config;
import org.platform.icache.Cache;
import org.platform.icache.DefaultCache;
import org.platform.icache.ICache;
import org.platform.icache.IKeyBuilder;
import org.platform.icache.KeyBuilder;
import org.platform.icache.NeedCache;
@InitListener(displayName = "TestModuleServiceListener", description = "org.platform.security软件包功能支持。", startIndex = 10)
public class TestModuleServiceListener extends AbstractModuleListener {
    private AppContext appContext = null;
    @Override
    public void initialize(ApiBinder event) {
        event.serve("*.cache").with(new HttpServlet() {
            private static final long serialVersionUID = 3679312570306989024L;
            @Override
            protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
                ParamBean pb_1 = new ParamBean("zyc_1");
                ParamBean pb_2 = new ParamBean("zyc_2");
                ParamBean pb_3 = new ParamBean("zyc_3");
                // 
                TestBean bean = appContext.getBean(TestBean.class);
                arg1.getWriter().append("eval1--------------------\n");
                arg1.getWriter().append(bean.eval1(pb_1) + "\n");
                arg1.getWriter().append(bean.eval1(pb_1) + "\n");
                arg1.getWriter().append(bean.eval1(pb_1) + "\n");
                arg1.getWriter().append("eval2--------------------\n");
                arg1.getWriter().append(bean.eval2(pb_1) + "\n");
                arg1.getWriter().append(bean.eval2(pb_1) + "\n");
                arg1.getWriter().append(bean.eval2(pb_1) + "\n");
            }
        });
    }
    @Override
    public void initialized(AppContext appContext) {
        this.appContext = appContext;
        super.initialized(appContext);
    }
}
class ParamBean {
    public ParamBean(String string) {
        this.value = string;
    }
    public String value = null;
}
class TestBean {
    int index = 0;
    @NeedCache
    public String eval1(ParamBean val) {
        index++;
        return val + String.valueOf(this.index);
    }
    public String eval2(ParamBean val) {
        index++;
        return val + String.valueOf(this.index);
    }
}
@KeyBuilder(ParamBean.class)
class ObjectIntKeyBuilder implements IKeyBuilder {
    @Override
    public void initKeyBuilder(AppContext appContext, Config config) {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
    @Override
    public String serializeKey(Object arg) {
        ParamBean b = (ParamBean) arg;
        return b.value;
    }
}
@DefaultCache
@Cache("map")
class MapCache extends HashMap<String, Object> implements ICache {
    @Override
    public void initCache(AppContext appContext, Config config) {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
    @Override
    public void toCache(String key, Object value) {
        this.toCache(key, value, 10000);
    }
    @Override
    public void toCache(String key, Object value, long timeout) {
        this.put(key, value);
    }
    @Override
    public Object fromCache(String key) {
        return this.get(key);
    }
    @Override
    public boolean hasCache(String key) {
        return this.containsKey(key);
    }
    @Override
    public void remove(String key) {
        super.remove(key);
    }
}