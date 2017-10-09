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
package net.hasor.core;
/**
 * 辅助{@link Inject @Inject}注解用来标识value，表示的是 ByID，还是ByName。
 * @version : 2015年7月28日
 * @author 赵永春(zyc@hasor.net)
 */
public enum Type {
    /**AppContext.getInstance(bindID)方式*/
    ByID,//
    /**（默认）AppContext.findBindingBean(withName, bindType)方式*/
    ByName
}