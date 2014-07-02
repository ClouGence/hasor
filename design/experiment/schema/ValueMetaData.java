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
import java.util.HashMap;
import java.util.Map;
/**
 * 表示一个属性值的抽象类
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ValueMetaData extends AbstractDefine {
    /*除了值和类型之外配置的其他属性，这些扩展配置属性的目的是当解析它的时候会使用。或用于携带信息。*/
    private Map<String, String> extParams = new HashMap<String, String>();
    /*------------------------------------------------------------------*/
    /**获取值类型*/
    public abstract String getType();
    /**获取配置的其他属性（这些扩展配置属性的目的是当解析它的时候会使用。或用于携带信息）*/
    public Map<String, String> getExtParams() {
        return extParams;
    }
    /**设置配置的其他属性（这些扩展配置属性的目的是当解析它的时候会使用。或用于携带信息）*/
    public void setExtParams(Map<String, String> extParams) {
        this.extParams = extParams;
    }
}