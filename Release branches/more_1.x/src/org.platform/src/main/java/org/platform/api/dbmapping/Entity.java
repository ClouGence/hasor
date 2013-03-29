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
package org.platform.api.dbmapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 定义一个实体对象
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Entity {
    /**实体名称，默认使用简短类名作为该值。*/
    public String name() default "";
    /**实体映射的表名，默认使用简短类名作为该值。*/
    public String table() default "";
    /** 模式，有三个值可以设置。
     * @see org.platform.api.dbmapping.Entity.Mode*/
    public Mode mode() default Mode.Mapping;
    /**该值是决定这个实体类的默认懒加载属性,默认值：false。 */
    public boolean lazy() default false;
    /**实体是否参允许更新保存。*/
    public boolean update() default true;
    /**实体是否允许新增保存。*/
    public boolean insert() default true;
    /**实体是否允许执行删除操作。*/
    public boolean delete() default true;
    /**配置扩展表信息。*/
    public ExtTable extTableInfo() default @ExtTable(extFKColumn = "", extPKColumn = "", extTable = "");
    /**
     * 每当重启系统，类实体的数据库映射会执行什么动作这个枚举的值定义。
     * @version : 2013-3-27
     * @author 赵永春 (zyc@byshell.org)
     */
    public static enum Mode {
        /**重构数据库对象，这种模式下会导致数据全部丢失。*/
        CreateDrop,
        /**自动更新数据库，这种模式下会最大限度保证数据不丢失的情况下更新数据库结构。*/
        Update,
        /**仅映射，这种模式不会执行任何数据库的DDL（数据库结构不会变化）语句。*/
        Mapping
    }
}