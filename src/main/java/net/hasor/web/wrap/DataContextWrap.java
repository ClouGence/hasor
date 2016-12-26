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
package net.hasor.web.wrap;
import net.hasor.core.AppContext;
import net.hasor.web.DataContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class DataContextWrap implements DataContext {
    private DataContext dataContext;
    //
    public DataContextWrap(DataContext dataContext) {
        this.dataContext = dataContext;
    }
    @Override
    public AppContext getAppContext() {
        return this.dataContext.getAppContext();
    }
    @Override
    public HttpServletRequest getHttpRequest() {
        return this.dataContext.getHttpRequest();
    }
    @Override
    public HttpServletResponse getHttpResponse() {
        return this.dataContext.getHttpResponse();
    }
    @Override
    public Set<String> keySet() {
        return this.dataContext.keySet();
    }
    @Override
    public Object get(String key) {
        return this.dataContext.get(key);
    }
    @Override
    public void remove(String key) {
        this.dataContext.remove(key);
    }
    @Override
    public void put(String key, Object value) {
        this.dataContext.put(key, value);
    }
    @Override
    public void lockKey(String key) {
        this.dataContext.lockKey(key);
    }
    @Override
    public String getMimeType(String suffix) {
        return this.dataContext.getMimeType(suffix);
    }
    @Override
    public String renderTo() {
        return this.dataContext.renderTo();
    }
    @Override
    public void renderTo(String viewName) {
        this.dataContext.renderTo(viewName);
    }
    @Override
    public void renderTo(String viewType, String viewName) {
        this.dataContext.renderTo(viewType, viewName);
    }
    @Override
    public String viewType() {
        return this.dataContext.viewType();
    }
    @Override
    public void viewType(String viewType) {
        this.dataContext.viewType(viewType);
    }
    @Override
    public boolean layout() {
        return this.dataContext.layout();
    }
    @Override
    public void layoutEnable() {
        this.dataContext.layoutEnable();
    }
    @Override
    public void layoutDisable() {
        this.dataContext.layoutDisable();
    }
}