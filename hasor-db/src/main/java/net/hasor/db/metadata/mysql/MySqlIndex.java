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
package net.hasor.db.metadata.mysql;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL 索引
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class MySqlIndex {
    private String              name;
    private MySqlIndexType      indexEnum;
    private List<String>        columns     = new ArrayList<>();
    private Map<String, String> storageType = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MySqlIndexType getIndexEnum() {
        return indexEnum;
    }

    public void setIndexEnum(MySqlIndexType indexEnum) {
        this.indexEnum = indexEnum;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Map<String, String> getStorageType() {
        return storageType;
    }

    public void setStorageType(Map<String, String> storageType) {
        this.storageType = storageType;
    }
}
