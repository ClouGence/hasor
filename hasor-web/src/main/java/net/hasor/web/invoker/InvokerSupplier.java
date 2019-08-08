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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.Mapping;
import net.hasor.web.MimeType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link Invoker} 接口实现类。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerSupplier implements Invoker {
    private Set<String>         lockKeys       = new HashSet<>();
    private HttpServletRequest  httpRequest    = null;
    private HttpServletResponse httpResponse   = null;
    private AppContext          appContext     = null;
    private MimeType            mimeType       = null;
    private Mapping             ownerInMapping = null;

    protected InvokerSupplier(Mapping ownerInMapping, AppContext appContext, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        //
        this.ownerInMapping = ownerInMapping;
        this.appContext = appContext;
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.mimeType = appContext.getInstance(MimeType.class);
        //
        this.put(ROOT_DATA_KEY, this);
        this.put(REQUEST_KEY, this.httpRequest);
        this.put(RESPONSE_KEY, this.httpResponse);
        //
        this.lockKey(ROOT_DATA_KEY);// rootData
        this.lockKey(REQUEST_KEY);  // response
        this.lockKey(RESPONSE_KEY); // request
    }

    @Override
    public AppContext getAppContext() {
        return appContext;
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return this.httpRequest;
    }

    @Override
    public HttpServletResponse getHttpResponse() {
        return this.httpResponse;
    }

    @Override
    public Mapping ownerMapping() {
        return this.ownerInMapping;
    }

    @Override
    public boolean isLockKey(String key) {
        return this.lockKeys.contains(key);
    }

    @Override
    public void lockKey(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        this.lockKeys.add(key);
    }

    @Override
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }

    @Override
    public <T> T fillForm(Class<? extends T> formType, T bean) {
        return new InvokerCallerParamsBuilder().getParamsParam(this, formType, bean);
    }
}