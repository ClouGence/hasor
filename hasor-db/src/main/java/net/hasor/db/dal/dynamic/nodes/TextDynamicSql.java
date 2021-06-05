/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.dal.dynamic.nodes;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.dynamic.segment.SqlSegmentParser;
import net.hasor.utils.StringUtils;

import java.sql.SQLException;

/**
 * 文本块
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class TextDynamicSql implements DynamicSql {
    private final StringBuilder textBuilder = new StringBuilder();
    private       DynamicSql    dynamicSql;

    public TextDynamicSql(String text) {
        this.appendText(StringUtils.isBlank(text) ? "" : text);
    }

    public void appendText(String text) {
        if (StringUtils.isNotBlank(text)) {
            this.textBuilder.append(text);
        }
        this.dynamicSql = parserQuery(this.textBuilder.toString());
    }

    @Override
    public boolean isHavePlaceholder() {
        return this.dynamicSql.isHavePlaceholder();
    }

    @Override
    public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
        this.dynamicSql.buildQuery(builderContext, querySqlBuilder);
    }

    protected DynamicSql parserQuery(String fragmentString) {
        return SqlSegmentParser.analysisSQL(fragmentString);
    }
}