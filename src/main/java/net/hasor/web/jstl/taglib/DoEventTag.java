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
package net.hasor.web.jstl.taglib;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
/**
 *
 * @version : 2013-12-24
 * @author 赵永春(zyc@hasor.net)
 */
public class DoEventTag extends AbstractTag {
    private static final long    serialVersionUID = -5728789063520917320L;
    private              String  event            = null;
    private              boolean async            = false;
    private              Object  params           = null;
    public String getEvent() {
        return this.event;
    }
    public void setEvent(final String event) {
        this.event = event;
    }
    public boolean isAsync() {
        return this.async;
    }
    public void setAsync(final boolean async) {
        this.async = async;
    }
    public Object getParams() {
        return this.params;
    }
    public void setParams(final Object params) {
        this.params = params;
    }
    //
    @Override
    public void release() {
        this.event = null;
        this.async = false;
        this.params = null;
    }
    private Object[] params() {
        if (this.params == null) {
            return null;
        }
        if (this.params.getClass().isArray()) {
            return (Object[]) this.params;
        }
        return new Object[] { this.params };
    }
    @Override
    public int doStartTag() throws JspException {
        try {
            if (this.async) {
                this.getAppContext().getEnvironment().getEventContext().fireAsyncEvent(this.event, this.params());
            } else {
                this.getAppContext().getEnvironment().getEventContext().fireSyncEvent(this.event, this.params());
            }
            return Tag.SKIP_BODY;
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            if (e instanceof JspException) {
                throw (JspException) e;
            }
            throw new JspException(e);
        }
    }
}