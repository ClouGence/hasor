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
package net.hasor.core.binder.schema;
import java.util.List;
/**
 * 表示一个{@link List}类型的值元信息描述。
 * @version 2010-9-17
 * @author 赵永春 (zyc@byshell.org)
 */
public class List_ValueMetaData extends Collection_ValueMetaData<ValueMetaData> {
    /**返回{@link PropertyType#List}*/
    @Override
    public String getType() {
        return PropertyType.List.value();
    }
}