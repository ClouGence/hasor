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
import java.util.HashMap;
/**
 * 用于 ASM 指令在处理方法返回值时。对数据的封装。
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-07-19
 */
public class ObjectResultStruts implements ResultStruts {
    private HashMap<String, Object> dataResult = null;
    //
    public ObjectResultStruts(Object toType) {
        this.dataResult = new HashMap<String, Object>();
    }
    //
    /** 添加结果 */
    public void addResultField(String filedName, Object data) {
        this.dataResult.put(filedName, data);
    }
    //
    @Override
    public Object getResult() {
        return this.dataResult;
    }
}