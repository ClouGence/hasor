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
package net.hasor.dataway.daos.mysql;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.function.Function;

/**
 * DAO 层接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public abstract class AbstractDal {
    /** 不参与更新的列 */
    protected static final Set<String>                             wontUpdateColumn = new HashSet<String>() {{
        add("api_id");
        add("api_method");
        add("api_path");
        add("api_create_time");
        //
        add("pub_id");
        add("pub_api_id");
        add("pub_method");
        add("pub_path");
        add("pub_type");
        add("pub_comment");
        add("pub_script");
        add("pub_script_ori");
        add("pub_schema");
        add("pub_sample");
        add("pub_option");
        add("pub_release_time");
    }};
    /** 列对应数据类型 */
    protected static final Map<String, Class<?>>                   columnTypes      = new HashMap<String, Class<?>>() {{
        put("api_id", String.class);
        put("api_method", String.class);
        put("api_path", String.class);
        put("api_status", Integer.class);
        put("api_comment", String.class);
        put("api_type", String.class);
        put("api_script", String.class);
        put("api_schema", String.class);
        put("api_sample", String.class);
        put("api_option", String.class);
        put("api_create_time", Date.class);
        put("api_gmt_time", Date.class);
        //
        put("pub_id", String.class);
        put("pub_api_id", String.class);
        put("pub_method", String.class);
        put("pub_path", String.class);
        put("pub_status", Integer.class);
        put("pub_comment", String.class);
        put("pub_type", String.class);
        put("pub_script", String.class);
        put("pub_script_ori", String.class);
        put("pub_schema", String.class);
        put("pub_sample", String.class);
        put("pub_option", String.class);
        put("pub_release_time", Date.class);
    }};
    /** target列对应数据类型 */
    protected static final Map<Class<?>, Function<String, Object>> targetConvert    = new HashMap<Class<?>, Function<String, Object>>() {{
        put(String.class, s -> s);
        put(Integer.class, String::valueOf);
        put(Date.class, s -> new Date(Long.parseLong(s)));
    }};
    @Inject
    protected              JdbcTemplate                            jdbcTemplate;
}