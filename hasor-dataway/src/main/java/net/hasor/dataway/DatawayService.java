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
package net.hasor.dataway;
import java.util.Map;

/**
 * 程序层面调用 API。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public interface DatawayService {
    /** 执行配置的接口。*/
    public Object invokeApi(String method, String apiPath, Map<String, Object> jsonParam) throws Throwable;
}