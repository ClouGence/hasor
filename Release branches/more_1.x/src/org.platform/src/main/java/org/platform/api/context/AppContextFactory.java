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
package org.platform.api.context;
/**
 * AppContext环境接口工厂类，该类的主要职责是为框架平台提供{@link AppContext}接口对象。
 * @version : 2013-3-28
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AppContextFactory {
    /**获取一个Appcontext对象。*/
    public abstract AppContext getAppContext();
    public abstract void initFactory(ContextConfig config);
}