/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.lambda.dialect;
import net.hasor.db.lambda.mapping.FieldInfo;

import java.util.List;

/**
 * 扩展了 SqlDialect 接口增加了 insert 的更多特性生成。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface InsertSqlDialect extends SqlDialect {
    /** 是否支持 insert ignore */
    public boolean supportInsertIgnore();

    public String insertWithIgnore(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields);

    /** 是否支持 insert replace */
    public boolean supportInsertReplace();

    public String insertWithReplace(boolean useQualifier, String category, String tableName, List<FieldInfo> pkFields, List<FieldInfo> insertFields);
}
