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
package org.more.submit.impl;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.more.submit.ActionObject;
import org.more.submit.ActionPackage;
/**
 *  默认{@link ActionObject}接口实现。
 * @version : 2011-7-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultPackage implements ActionPackage {
    private String              packageName = null;
    private Map<String, Method> mapping     = new HashMap<String, Method>();
    public DefaultPackage(String packageName) {
        this.packageName = packageName;
    };
    public String packageName() {
        return this.packageName;
    };
    public void addRouteMapping(String mappingPath, Method actionPath) {
        if (mappingPath.charAt(0) != '/')
            this.mapping.put("/" + mappingPath, actionPath);
        else
            this.mapping.put(mappingPath, actionPath);
    };
    public Method getActionPath(String mappingPath) {
        return this.mapping.get(mappingPath);
    };
};