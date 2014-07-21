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
package net.hasor.core.context.factorys.hasor.schema;
/**
 * 表示一个bean定义中的一种参数
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ParamDefine extends AbstractPropertyDefine {
    /*属性索引，负数为自动分配*/
    private int    index = -1;
    /*属性名*/
    private String name  = null;
    /*------------------------------------------------------------------*/
    /**获取属性索引，负数为自动分配*/
    public int getIndex() {
        return this.index;
    }
    /**设置属性索引，负数为自动分配*/
    public void setIndex(final int index) {
        this.index = index;
    }
    /**返回属性名。*/
    public String getName() {
        return this.name;
    }
    /**设置属性名*/
    public void setName(final String name) {
        this.name = name;
    }
    /**返回具有特征的字符串。*/
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode() + " index=" + this.getIndex();
    }
}