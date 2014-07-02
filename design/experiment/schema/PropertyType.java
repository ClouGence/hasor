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
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Date;
/**
 * 该枚举中定义了{@link Simple_ValueMetaData}类可以表示的基本类型。
 * @version 2010-11-11
 * @author 赵永春 (zyc@byshell.org)
 */
public enum PropertyType {
    /**null数据。*/
    Null("vt:null"),
    /**布尔类型。*/
    Boolean("vt:boolean"),
    /**字节类型。*/
    Byte("vt:byte"),
    /**短整数类型。*/
    Short("vt:short"),
    /**整数类型。*/
    Int("vt:int"),
    /**长整数类型。*/
    Long("vt:long"),
    /**单精度浮点数类型。*/
    Float("vt:float"),
    /**双精度浮点数类型。*/
    Double("vt:double"),
    /**字符类型。*/
    Char("vt:char"),
    /**字符串类型。*/
    String("vt:string"),
    //
    /**数组类型。*/
    Array("vt:array"),
    /**集合类型。*/
    List("vt:list"),
    /**Set类型。*/
    Set("vt:set"),
    /**Map类型。*/
    Map("vt:map"),
    /**Map的一个实体类型。*/
    MapEntity("vt:entity"),
    //
    /**Map类型。*/
    Ref("vt:ref"),
    //
    /**Json数据类型。*/
    Json("vt:json"),
    /**{@link URL}类型。*/
    URL("vt:url"),
    /**{@link URI}类型。*/
    URI("vt:uri"),
    /**{@link File}类型。*/
    File("vt:file"),
    /**{@link Date}类型。*/
    Date("vt:date"), ;
    /*------------------------------------------------------------------*/
    private String value = null;
    PropertyType(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}