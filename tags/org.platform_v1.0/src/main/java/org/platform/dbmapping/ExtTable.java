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
package org.platform.dbmapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 扩展表的配置
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface ExtTable {
    /**用于存放扩展属性表的表名。*/
    public String extTable();
    /**主键列，主键列要求是varchar。*/
    public String extPKColumn();
    /**扩展属性表用于关联到主体对象上的外键列。*/
    public String extFKColumn();
    /**key字段，仅在extMode处于Row模式下有效。*/
    public String extKeyColumn() default "key";
    /**var字段，仅在extMode处于Row模式下有效。*/
    public String extVarColumn() default "var";
}