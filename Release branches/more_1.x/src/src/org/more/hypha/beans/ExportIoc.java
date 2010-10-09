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
package org.more.hypha.beans;
import org.more.hypha.ApplicationContext;
/**
 * 属性注入请求处理接口，该接口负责对某个bean进行复杂注入请求的处理。
 * @version 2010-9-18
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ExportIoc {
    /**
     * 执行注入的方法，在该接口中完成属性注入的过程。
     * @param object 要注入的目标属性。
     * @param initParam 创建bean的启动参数。
     * @param define bean的定义信息。
     * @param context 应用环境
     */
    public Object iocProcess(Object object, Object[] initParam, AbstractBeanDefine define, ApplicationContext context);
}