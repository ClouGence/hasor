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
package net.hasor.web;
import net.hasor.core.AppContext;

import java.util.Enumeration;
/**
 * @version : 2016-12-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface InvokerConfig {
    /**
     * Returns a <code>String</code> containing the value of the
     * named initialization parameter, or <code>null</code> if
     * the initialization parameter does not exist.
     *
     * @param name a <code>String</code> specifying the name of the
     * initialization parameter
     *
     * @return a <code>String</code> containing the value of the
     * initialization parameter, or <code>null</code> if
     * the initialization parameter does not exist
     */
    public String getInitParameter(String name);

    /**
     * Returns the names of the filter's initialization parameters
     * as an <code>Enumeration</code> of <code>String</code> objects,
     * or an empty <code>Enumeration</code> if the filter has
     * no initialization parameters.
     *
     * @return an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of the filter's initialization parameters
     */
    public Enumeration<String> getInitParameterNames();

    public AppContext getAppContext();
}