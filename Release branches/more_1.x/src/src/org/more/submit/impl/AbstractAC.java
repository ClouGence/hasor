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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.more.core.error.FormatException;
import org.more.core.error.ResourceException;
import org.more.submit.ActionContext;
import org.more.submit.ActionInvoke;
import org.more.submit.ActionPackage;
/**
 * 基础AC实现。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractAC implements ActionContext {
    private Map<String, ActionPackage> packages = new HashMap<String, ActionPackage>();
    //
    public ActionPackage definePackage(String packageName) {
        if (packageName == null)
            packageName = "";
        if (packageName.indexOf('/') > 0)
            throw new FormatException("包名中不能存在字符‘/’");
        ActionPackage pack = this.packages.get(packageName);
        if (pack == null) {
            pack = new DefaultPackage(packageName);
            this.packages.put(packageName, pack);
        }
        return pack;
    };
    public ActionInvoke getAction(URI uri) {
        //先从默认包中获取actio路径。
        String name = uri.getAuthority();
        String actionPath = this.definePackage("").getActionPath(name);
        if (actionPath == null)
            //name作为包名获取action地址。
            actionPath = this.definePackage(name).getActionPath(uri.getPath());
        if (actionPath == null)
            throw new ResourceException("默认包中不存在该action映射，同时也没有定义该名称的包。[" + name + "]");
        //
        return this.findAction(actionPath, uri.getQuery());
    };
    public abstract ActionInvoke findAction(String actionPath, String queryInfo);
};