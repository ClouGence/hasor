/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.db.dialect;
/**
 * 扩展了 SqlDialect 接口增加了 insert 多记录的方言
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public interface MultipleInsertSqlDialect extends SqlDialect {
    /** 开始多记录 insert */
    public String multipleRecordInsertPrepare();

    /** 用于切分多个记录的字符 */
    public String multipleRecordInsertSplitRecord();

    /** 开始一组值 */
    public String multipleRecordInsertBeforeValues(boolean firstRecord, String tableNameAndColumn);

    /** 结束一组值 */
    public String multipleRecordInsertAfterValues();

    /** 结束多记录 insert */
    public String multipleRecordInsertFinish();
}
