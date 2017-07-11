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

import java.io.IOException;
import java.io.Writer;
/**
 * 渲染引擎
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RenderEngine {
    /** 初始化引擎 */
    public void initEngine(AppContext appContext) throws Throwable;

    /** 执行模版引擎 */
    public void process(RenderInvoker invoker, Writer writer) throws Throwable;

    /**
     * exist 的作用是用来在 process 执行之前，让渲染器检查一下，要执行的 模板是否存在。如果不存在就不会执行 process。
     */
    public boolean exist(String template) throws IOException;
}