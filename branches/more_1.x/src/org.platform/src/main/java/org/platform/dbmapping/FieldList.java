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
 * 表示多对一的映射关系。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface FieldList {
    /**映射的表列名。*/
    public String column() default "";
    /**该字段是否允许空值。*/
    public boolean isNull() default true;
    /**最大设置长度。*/
    public int length() default 1000;
    /**默认值。*/
    public String defaultValue() default "";
    /**字段是否参与更新。*/
    public boolean updateMode() default true;
    /**字段是否参与插入。*/
    public boolean insertMode() default true;
    /**字段是否为懒加载。*/
    public boolean lazy() default false;
    /**数据库使用的数据类型。*/
    public DBType dbType() default DBType.Nvarchar;
    //
    //
    /**设置目标实体的一个属性字段，该字段可以和这个字段组成外键关联。*/
    public String forProperty();
    /**目标实体集合的顺序依照的属性名。*/
    public String sortBy() default "";
    /**目标实体集合的顺序方式。*/
    public String sortMode() default "asc";
    /**附加过滤条件。*/
    public String filter() default "";//"this.userName like 'abc%' and this.attGroup.abc='A'";
}