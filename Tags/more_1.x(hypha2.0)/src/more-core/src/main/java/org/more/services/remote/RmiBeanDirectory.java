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
package org.more.services.remote;
import java.util.Map;
/**
 * 该接口是一个可以批量提供{@link RmiBeanCreater}的接口。
 * @version : 2011-8-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface RmiBeanDirectory {
    /**返回要注册的所有{@link RmiBeanCreater}接口对象。*/
    public Map<String, RmiBeanCreater> getCreaterMap() throws Throwable;
}