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
package net.hasor.dataway.dal.db;
import net.hasor.core.Inject;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * DAO 层接口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public abstract class AbstractDal {
    /** 不参与更新的列 */
    protected static final Set<String>  wontUpdateColumn = new HashSet<String>() {{
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
    @Inject
    protected              JdbcTemplate jdbcTemplate;
    protected              String       dbType;

    protected String fixString(String val) {
        if (JdbcUtils.ORACLE.equalsIgnoreCase(this.dbType) && StringUtils.isBlank(val)) {
            // Oracle 下 NULL 和 '' 是一个意思 - see：https://www.cnblogs.com/memory4young/p/use-null-empty-space-in-oracle.html
            return "there is no comment";
        } else {
            return val;
        }
    }
}