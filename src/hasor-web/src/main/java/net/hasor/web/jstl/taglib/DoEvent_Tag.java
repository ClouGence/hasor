/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.web.jstl.taglib;
import javax.servlet.jsp.JspException;
/**
 * 
 * @version : 2013-12-24
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class DoEvent_Tag extends AbstractHasorTag {
    private static final long serialVersionUID = -5728789063520917320L;
    private String            event            = null;
    private boolean           async            = false;
    private Object            params           = null;
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public boolean isAsync() {
        return async;
    }
    public void setAsync(boolean async) {
        this.async = async;
    }
    public Object getParams() {
        return params;
    }
    public void setParams(Object params) {
        this.params = params;
    }
    //
    public void release() {
        this.event = null;
        this.async = false;
        this.params = null;
    }
    public int doStartTag() throws JspException {
        try {
            if (async == true) {
                getAppContext().fireAsyncEvent(event, params);
            } else {
                getAppContext().fireSyncEvent(event, params);
            }
            return SKIP_BODY;
        } catch (Throwable e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            if (e instanceof JspException)
                throw (JspException) e;
            throw new JspException(e);
        }
    }
}