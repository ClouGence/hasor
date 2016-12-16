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
package net.hasor.restful.wrap;
import net.hasor.core.AppContext;
import net.hasor.restful.RenderData;
import org.more.bizcommon.Message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
/**
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderDataWrap implements RenderData {
    private RenderData renderData;
    public RenderDataWrap(RenderData renderData) {
        this.renderData = renderData;
    }
    //
    @Override
    public AppContext getAppContext() {
        return this.renderData.getAppContext();
    }
    @Override
    public HttpServletRequest getHttpRequest() {
        return this.renderData.getHttpRequest();
    }
    @Override
    public HttpServletResponse getHttpResponse() {
        return this.renderData.getHttpResponse();
    }
    @Override
    public Set<String> keySet() {
        return this.renderData.keySet();
    }
    @Override
    public Object get(String key) {
        return this.renderData.get(key);
    }
    @Override
    public void remove(String key) {
        this.renderData.remove(key);
    }
    @Override
    public void put(String key, Object value) {
        this.renderData.put(key, value);
    }
    @Override
    public String renderTo() {
        return this.renderData.renderTo();
    }
    @Override
    public void renderTo(String viewName) {
        this.renderData.renderTo(viewName);
    }
    @Override
    public void renderTo(String viewType, String viewName) {
        this.renderData.renderTo(viewType, viewName);
    }
    @Override
    public String viewType() {
        return this.renderData.viewType();
    }
    @Override
    public void viewType(String viewType) {
        this.renderData.viewType(viewType);
    }
    @Override
    public boolean layout() {
        return this.renderData.layout();
    }
    @Override
    public void layoutEnable() {
        this.renderData.layoutEnable();
    }
    @Override
    public void layoutDisable() {
        this.renderData.layoutDisable();
    }
    @Override
    public List<String> validKeys() {
        return this.renderData.validKeys();
    }
    @Override
    public List<Message> validErrors(String messageKey) {
        return this.renderData.validErrors(messageKey);
    }
    @Override
    public boolean isValid() {
        return this.renderData.isValid();
    }
    @Override
    public boolean isValid(String messageKey) {
        return this.renderData.isValid(messageKey);
    }
    @Override
    public void clearValidErrors() {
        this.renderData.clearValidErrors();
    }
    @Override
    public void clearValidErrors(String messageKey) {
        this.renderData.clearValidErrors(messageKey);
    }
}