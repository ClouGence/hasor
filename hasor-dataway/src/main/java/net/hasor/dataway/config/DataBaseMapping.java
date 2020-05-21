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
import java.util.Arrays;
import java.util.Comparator;

/**
 * Dataway 使用的数据源类型
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
public enum DataBaseMapping {
    Mysql("MySQL", 0, "default"),//
    Oracle("Oracle", 0, "oracle"),//
    SqlServer2012("Microsoft SQL Server", 14, "sqlserver2012"),//
    PostgreSQL("PostgreSQL", 0, "postgresql");
    //
    //
    private String dbProductName = null;
    private int    minVersion    = 0;
    private String mappingType   = null;

    DataBaseMapping(String dbProductName, int minVersion, String mappingType) {
        this.dbProductName = dbProductName;
        this.minVersion = minVersion;
        this.mappingType = mappingType;
    }

    public static DataBaseMapping formName(DatawayModule.DbInfo dbInfo) {
        if (dbInfo == null) {
            return null;
        }
        String productTag = dbInfo.productName.toLowerCase();
        return Arrays.stream(DataBaseMapping.values())//
                .sorted(Comparator.comparingInt(o -> o.minVersion))//
                .filter(dataBaseMapping -> {
                    boolean productTest = productTag.contains(dataBaseMapping.dbProductName.toLowerCase());
                    boolean versionTest = dbInfo.majorVersion >= dataBaseMapping.minVersion;
                    return productTest && versionTest;
                }).findFirst().orElse(null);
    }

    public String mappingType() {
        return this.mappingType;
    }
}