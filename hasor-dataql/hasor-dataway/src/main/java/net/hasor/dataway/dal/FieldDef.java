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
package net.hasor.dataway.dal;
/**
 * 底层存储上的实体中含有的字段
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-11
 */
public enum FieldDef {
    ID,                 //
    METHOD,             //
    PATH,               //
    STATUS,             //
    COMMENT,            //
    TYPE,               //
    SCRIPT,             //
    //
    REQ_BODY_SCHEMA,    //
    RES_BODY_SCHEMA,    //
    REQ_HEADER_SCHEMA,  //
    RES_HEADER_SCHEMA,  //
    //
    REQ_HEADER_SAMPLE,  //
    RES_HEADER_SAMPLE,  //
    REQ_BODY_SAMPLE,    //
    RES_BODY_SAMPLE,    //
    //
    OPTION,             //
    PREPARE_HINT,       //
    CREATE_TIME,        //
    GMT_TIME,           //
    //
    API_ID,             //
    SCRIPT_ORI,         //
    RELEASE_TIME,       //
}