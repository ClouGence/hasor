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
/**
 * 基于数据库元信息的框架，包括基于底层元信息表的数据库元信息查询获取数据库对象生成。
 *
 * 主要关注的是：库、表(含视图)、列、索引、Schema、PK、FK、UK <br/>
 * 具有一定程度上的 JDBC MetaData 可替代性，但是不同于 JDBC MetaData 的是 hasor.metadata 保留了对数据库差异性的支持。
 * <li>ColumnDef<li/>
 * <li>SqlType<li/>
 * <li>TableDef<li/>
 * <li>TableType<li/>
 * 例如 SqlType 将不同数据库的类型，在保留差异性的前一下统一类型接口。 JDBCTypes 则属于建立另外一套类型规范，数据库厂商的类型需要映射过来。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-01-22
 */
@IgnoreProxy
package net.hasor.db.metadata;
import net.hasor.core.IgnoreProxy;
