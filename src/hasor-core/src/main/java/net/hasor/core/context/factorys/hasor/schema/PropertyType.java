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
import java.io.File;
import java.net.URI;
import java.net.URL;
/**
 * 该枚举中定义了{@link Simple_ValueMetaData}类可以表示的基本类型。
 * @version 2010-11-11
 * @author 赵永春 (zyc@byshell.org)
 */
public enum PropertyType {
    /**null数据。*/
    Null("null"),
    /**布尔类型。*/
    Boolean("boolean"),
    /**字节类型。*/
    Byte("byte"),
    /**短整数类型。*/
    Short("short"),
    /**整数类型。*/
    Int("int"),
    /**长整数类型。*/
    Long("long"),
    /**单精度浮点数类型。*/
    Float("float"),
    /**双精度浮点数类型。*/
    Double("double"),
    /**字符类型。*/
    Char("char"),
    /**字符串类型。*/
    String("string"),
    //
    /**数组类型。*/
    Array("array"),
    /**集合类型。*/
    List("list"),
    /**Set类型。*/
    Set("set"),
    /**Map类型。*/
    Map("map"),
    /**Map的一个实体类型。*/
    MapEntity("entity"),
    //
    /**Map类型。*/
    Ref("ref"),
    //
    /**Json数据类型。*/
    Json("json"),
    /**{@link URL}类型。*/
    URL("url"),
    /**{@link URI}类型。*/
    URI("uri"),
    /**{@link File}类型。*/
    File("file"), ;
    /*------------------------------------------------------------------*/
    private String value = null;
    PropertyType(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}