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
package net.hasor.dataql.fx.basic;
import com.alibaba.fastjson.JSON;
import net.hasor.dataql.UdfSourceAssembly;

import javax.inject.Singleton;

/**
 * Json函数。函数库引入 <code>import 'net.hasor.dataql.fx.basic.JsonUdfSource' as json;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
public class JsonUdfSource implements UdfSourceAssembly {
    /** 把对象 JSON 序列化 */
    public String toJson(Object data) {
        return JSON.toJSONString(data);
    }

    /** 把对象 JSON 序列化（带格式） */
    public String toFmtJson(Object data) {
        return JSON.toJSONString(data, true);
    }

    /** 解析 JSON 序列化（带格式） */
    public Object fromJson(String data) {
        if (data == null) {
            return null;
        }
        return JSON.parse(data);
    }
}