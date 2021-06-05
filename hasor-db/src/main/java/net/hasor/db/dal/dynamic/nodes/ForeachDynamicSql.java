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
package net.hasor.db.dal.dynamic.nodes;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.dynamic.ognl.OgnlUtils;
import net.hasor.utils.StringUtils;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * 对应XML中 <foreach>
 * @author jmxd
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class ForeachDynamicSql extends ArrayDynamicSql {
    /** 数据集合、支持Collection、数组 */
    private final String collection;
    /** item 变量名 */
    private final String item;
    /** 拼接起始SQL */
    private final String open;
    /** 拼接结束SQL */
    private final String close;
    /** 分隔符 */
    private final String separator;

    public ForeachDynamicSql(String collection, String item, String open, String close, String separator) {
        this.collection = collection;
        this.item = item;
        this.open = open;
        this.close = close;
        this.separator = separator;
    }

    @Override
    public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
        // 获取集合数据对象，数组形态
        Object collectionData = OgnlUtils.evalOgnl(this.collection, builderContext.getContext());
        if (collectionData == null) {
            return;
        }
        if (collectionData instanceof Collection) {
            collectionData = ((Collection<?>) collectionData).toArray();
        }
        if (!collectionData.getClass().isArray()) {
            collectionData = new Object[] { collectionData }; //如果不是数组那么转换成数组
        }
        //
        querySqlBuilder.appendSql(StringUtils.defaultString(this.open));
        Map<String, Object> objectMap = builderContext.getContext();
        Object oriValue = objectMap.get(this.item);
        try {
            int length = Array.getLength(collectionData);
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    querySqlBuilder.appendSql(StringUtils.defaultString(this.separator)); // 分隔符
                }
                objectMap.put(this.item, Array.get(collectionData, i));
                super.buildQuery(builderContext, querySqlBuilder);
            }
            querySqlBuilder.appendSql(StringUtils.defaultString(this.close));
        } finally {
            objectMap.put(this.item, oriValue);
        }
    }
}
