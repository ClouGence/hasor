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
package org.more.hypha.beans;
/**
 * 该枚举定义了{@link ValueMetaData}属性元信息表述的属性类型范畴，
 * 该类型中包含了基本类型范畴各种集合类型范畴以及一些其他类型范畴。
 * @version 2011-1-24
 * @author 赵永春 (zyc@byshell.org)
 */
public interface PropertyMetaTypeEnum {
    /**
     * 一个简单类型属性类型，包括字符串在内的所有java基本类型都可以由SimpleType来表示。
     * 被列为简单类型的有：null,boolean,byte,short,int,long,float,double,char,string。
     */
    public static String SimpleType      = "SimpleType";
    /**表示一个枚举类型*/
    public static String Enum            = "Enum";
    /**表示对一个bean的引用*/
    public static String RelationBean    = "RelationBean";
    /**表示一个数组类型集合。*/
    public static String ArrayCollection = "ArrayCollection";
    /**表示一个List类型集合。*/
    public static String ListCollection  = "ListCollection";
    /**表示一个Set类型集合。*/
    public static String SetCollection   = "SetCollection";
    /**表示一个Map类型集合。*/
    public static String MapCollection   = "MapCollection";
    /**表示一个文件对象。*/
    public static String File            = "File";
    /**表示一个超级连接资源。*/
    public static String URI             = "URI";
    /**表示一个大文本对象。*/
    public static String BigText         = "BigText";
    /**表示一个日期对象。*/
    public static String Date            = "Date";
    /**表示一个el执行。*/
    public static String EL              = "EL";
};