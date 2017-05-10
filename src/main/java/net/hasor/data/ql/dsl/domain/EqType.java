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
package net.hasor.data.ql.dsl.domain;
/**
 *
 * @author 赵永春(zyc@hasor.net)
 * @version : 2017-03-23
 */
public enum EqType {
    /** = */
    EQ("="), //
    /** != */
    NE("!="),//
    /** > */
    GT(">"),//
    /** >= */
    GTE(">="),//
    /** < */
    LT("<"),//
    /** <= */
    LTE("<="),//
    /** like */
    Like("like"),;//
    //
    private String typeString;
    EqType(String type) {
        this.typeString = type;
    }
    public String getTypeString() {
        return typeString;
    }
    public static EqType paserType(String typeString) {
        for (EqType et : EqType.values()) {
            if (et.typeString.equalsIgnoreCase(typeString))
                return et;
        }
        return EQ;
    }
}