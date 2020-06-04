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
import net.hasor.dataql.Hints;

import java.util.Map;

/**
 * API 基础信息
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public interface DatawayApi {
    public String getApiID();

    public String getMethod();

    public String getApiPath();

    public Map<String, Object> getOptionMap();

    public Hints getPrepareHint();
}