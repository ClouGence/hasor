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
 * 表示一个数据库表的主键
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ID {
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
    public DBType dbType() default DBType.UUID;
    //
    //
    /**使用的主键生成策略。*/
    public String keyGenerator() default "uuid.string";
}