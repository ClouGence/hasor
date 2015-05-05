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
import net.hasor.core.Hasor;
import net.hasor.db.orm.ar.Record;
import net.hasor.db.orm.ar.Sechma;
import org.more.util.map.bean.BeanMap;
/**
 * 用来表示查询结果中的一条数据记录
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class ObjectRecord<T> extends Record {
    private static final long serialVersionUID = -6638591678248867068L;
    private T                 dataContainer    = null;
    private BeanMap           dataContainerMap = null;
    //
    /**创建{@link ObjectRecord}并用具体数据填充。*/
    public ObjectRecord(Sechma sechma, Class<T> recordType) throws InstantiationException, IllegalAccessException {
        this(sechma, recordType.newInstance());
    }
    /**创建{@link ObjectRecord}并用具体数据填充。*/
    public ObjectRecord(Sechma sechma, T dataContainer) {
        super(sechma);
        this.dataContainer = Hasor.assertIsNotNull(dataContainer);
        this.dataContainerMap = new BeanMap(dataContainer);
    }
    //
    /**获取数据容器。*/
    protected Map<String, Object> getDataContainer() {
        return this.dataContainerMap;
    }
    /**克隆一个新的{@link ObjectRecord}*/
    public Object clone() throws CloneNotSupportedException {
        return new ObjectRecord<T>(this.getSechma(), this.dataContainer);
    }
    /**按照列名获取数据。*/
    public Object get(String column) {
        return this.dataContainerMap.get(column);
    }
    /**按照列名获取数据。*/
    public Record set(String column, Object var) {
        this.dataContainerMap.put(column, var);
        return this;
    }
}