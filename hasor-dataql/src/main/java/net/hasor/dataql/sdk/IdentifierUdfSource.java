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
package net.hasor.dataql.sdk;
import net.hasor.dataql.Udf;
import net.hasor.dataql.UdfSource;

import java.util.Map;
import java.util.UUID;

/**
 * ID函数。函数库引入 <code>import 'net.hasor.dataql.sdk.IdentifierUdfSource' as ids;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
public class IdentifierUdfSource extends TypeUdfMap implements UdfSource {
    public IdentifierUdfSource() {
        super(IdentifierUdfSource.class);
    }

    @Override
    public Map<String, Udf> getUdfResource() {
        return this;
    }
    // ----------------------------------------------------------------------------------

    /** 返回一个完整格式的 UUID 字符串。  */
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    /** 返回一个不含"-" 符号的 UUID 字符串 */
    public String uuid2() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}