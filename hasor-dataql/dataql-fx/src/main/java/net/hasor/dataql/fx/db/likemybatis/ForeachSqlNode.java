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
package net.hasor.dataql.fx.db.likemybatis;
import net.hasor.db.dal.fxquery.DefaultFxQuery;
import net.hasor.utils.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 对应XML中 <foreach>
 * @author jmxd
 * @version : 2020-05-18
 */
public class ForeachSqlNode extends SqlNode {
    /** 数据集合，支持Collection、数组 */
    private String collection;
    /** item 变量名 */
    private String item;
    /** 拼接起始SQL */
    private String open;
    /** 拼接结束SQL */
    private String close;
    /** 分隔符 */
    private String separator;

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public String getSql(Map<String, Object> paramMap, List<Object> parameters) {
        // 提取集合
        Object value = DefaultFxQuery.evalOgnl(this.collection, paramMap);
        // 如果集合为空，则过滤该节点
        if (value == null) {
            return "";
        }
        // 开始拼接SQL,
        String sql = StringUtils.defaultString(this.open);
        // 如果集合是Collection对象或其子类，则转成数组
        if (value instanceof Collection) {
            value = ((Collection) value).toArray();
        }
        // 判断不是数组，则过滤子节点并返回
        if (!value.getClass().isArray()) {
            return "";
        }
        // 获取数组长度
        int len = Array.getLength(value);
        for (int i = 0; i < len; i++) {
            // 存入item对象
            paramMap.put(this.item, Array.get(value, i));
            // 拼接子节点
            sql += executeChildren(paramMap, parameters);
            // 拼接分隔符
            if (i + 1 < len) {
                sql += StringUtils.defaultString(this.separator);
            }
        }
        // 拼接结束SQL
        return sql + StringUtils.defaultString(this.close);
    }
}
