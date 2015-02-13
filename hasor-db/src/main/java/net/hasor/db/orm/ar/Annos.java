/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.db.orm.ar;
/**
 * 
 * @version : 2014年11月26日
 * @author 赵永春(zyc@hasor.net)
 */
public final class Annos {
    /**映射的表。*/
    public static @interface Table {
        public String value() default "";
    }
    /**映射的列。*/
    public static @interface Column {
        public String value() default "";
    }
    /**主键列。*/
    public static @interface PrimaryKey {}
    /**非空约束。*/
    public static @interface Null {
        public boolean value() default false;
    }
    /**大小限制。*/
    public static @interface Size {
        public int value() default 200;
    }
    /**字段的授权策略。*/
    public static @interface AllowPolicy {
        public boolean insert() default true;
        public boolean update() default true;
    }
}