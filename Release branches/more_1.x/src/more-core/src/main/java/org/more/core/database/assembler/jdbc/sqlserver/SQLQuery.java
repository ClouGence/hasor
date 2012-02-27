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
package org.more.core.database.assembler.jdbc.sqlserver;
import java.util.List;
import java.util.Map;
import org.more.core.database.PagesList;
import org.more.core.database.QueryCallBack;
import org.more.core.database.assembler.AbstractQuery;
/**
 * 通用查询接口实现类.
 * Date : 2010-6-21
 * @author 赵永春
 */
public class SQLQuery extends AbstractQuery<SQLDataBaseSupport> {
    public SQLQuery(String queryString, SQLDataBaseSupport support) {
        super(queryString, support);
    }
    @Override
    public List<Map<String, Object>> query(QueryCallBack callBack, Object... params) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public PagesList queryForPages(int pageSize, QueryCallBack callBack, Object... params) {
        // TODO Auto-generated method stub
        return null;
    }
};