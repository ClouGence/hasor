/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.orm.ar.record;
import java.util.Map;
import net.hasor.db.jdbc.core.LinkedCaseInsensitiveMap;
import net.hasor.db.orm.ar.Record;
import net.hasor.db.orm.ar.Sechma;
/**
 * 用来表示查询结果中的一条数据记录
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class MapRecord extends Record {
    private static final long   serialVersionUID = 7553043036092551721L;
    private Map<String, Object> dataContainer    = null;
    //
    /**创建{@link MapRecord}并用具体数据填充。*/
    public MapRecord(Sechma sechma) {
        this(sechma, null);
    }
    /**创建{@link MapRecord}并用具体数据填充。*/
    public MapRecord(Sechma sechma, Map<String, Object> dataContainer) {
        super(sechma);
        this.dataContainer = new LinkedCaseInsensitiveMap<Object>();
        if (dataContainer != null) {
            this.dataContainer.putAll(dataContainer);
        }
    }
    //
    /**获取数据容器。*/
    protected Map<String, Object> getDataContainer() {
        return this.dataContainer;
    }
    /**克隆一个新的{@link MapRecord}*/
    public Object clone() throws CloneNotSupportedException {
        return new MapRecord(this.getSechma(), this.dataContainer);
    }
    /**按照列名获取数据。*/
    public Object get(String column) {
        return this.dataContainer.get(column);
    }
    /**按照列名获取数据。*/
    public Record set(String column, Object var) {
        this.dataContainer.put(column, var);
        return this;
    }
}