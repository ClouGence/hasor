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
package net.hasor.dataql.runtime.struts;
import java.util.ArrayList;
import java.util.List;
/**
 * 用于 ASA 指令在处理方法返回值时。对数据的封装。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
public class ListResultStruts implements ResultStruts {
    private List<Object> dataResult = null;
    //
    public ListResultStruts(Object toType) {
        this.dataResult = new ArrayList<Object>();
    }
    //
    /**添加结果*/
    public void addResult(Object data) {
        this.dataResult.add(data);
    }
    //
    @Override
    public Object getResult() {
        return this.dataResult;
    }
}