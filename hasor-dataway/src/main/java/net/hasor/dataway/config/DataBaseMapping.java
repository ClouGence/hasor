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
package net.hasor.dataway.config;
/**
 * Dataway 使用的数据源类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
public enum DataBaseMapping {
    Mysql("MySQL", "default"),//
    Oracle("Oracle", "oracle"),//
    SqlServer2012("Microsoft SQL Server 14", "sqlserver2012"),//
    SqlServer("Microsoft SQL Server", "oracle"),//
    PostgreSQL("PostgreSQL", "default");
    //
    //
    private String dbProductName;
    private String mappingType;

    DataBaseMapping(String dbProductName, String mappingType) {
        this.dbProductName = dbProductName;
        this.mappingType = mappingType;
    }

    public static DataBaseMapping formName(String productName) {
        String productTag = productName.toLowerCase();
        for (DataBaseMapping mapping : DataBaseMapping.values()) {
            if (productTag.contains(mapping.dbProductName.toLowerCase())) {
                return mapping;
            }
        }
        return null;
    }

    public String mappingType() {
        return this.mappingType;
    }
}