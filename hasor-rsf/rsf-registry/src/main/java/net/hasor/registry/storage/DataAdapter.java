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
package net.hasor.registry.storage;
import java.util.List;
import java.util.function.Predicate;

/**
 * 服务数据存储检索适配器，负责将数据的操作对应到 DataDao 接口上。
 * @version : 2015年8月19日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DataAdapter {
    //
    public DataEntity readData(String dataKey) {
        return readData(new String[] { dataKey })[0];
    }

    public boolean deleteData(String dataKey) {
        return deleteData(new String[] { dataKey })[0];
    }

    public boolean exist(String dataKey) {
        return exist(new String[] { dataKey })[0];
    }

    //
    //
    public boolean[] exist(String[] dataKeys) {
        return null;
    }

    public DataEntity[] readData(String[] dataKey) {
        return null;
    }

    public boolean[] deleteData(String[] dataKey) {
        return null;
    }

    //
    public List<DataEntity> listData(String dataKey, Predicate<? extends DataEntity> matcher) {
        return null;
    }

    public boolean writeData(String dataKey, String dataValue, long tags) {
        return false;
    }

    public boolean linkTo(String dataKey, String mappingToDataKey, long tag) {
        return false;
    }
}