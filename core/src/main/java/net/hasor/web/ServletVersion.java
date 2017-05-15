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
package net.hasor.web;
/**
 * servlet版本
 * @version : 2016-12-26
 * @author 赵永春(zyc@hasor.net)
 */
public enum ServletVersion {
    V2_3(23), V2_4(24), V2_5(25), V3_0(30), V3_1(31),;
    //
    private int version;
    ServletVersion(int version) {
        this.version = version;
    }
    /**大于*/
    public boolean gt(ServletVersion otherVersion) {
        return this.version > otherVersion.version;
    }
    /**大于等于*/
    public boolean ge(ServletVersion otherVersion) {
        return this.version >= otherVersion.version;
    }
    //
    /**等于*/
    public boolean eq(ServletVersion otherVersion) {
        return this.version == otherVersion.version;
    }
    //
    /**小于*/
    public boolean lt(ServletVersion otherVersion) {
        return this.version < otherVersion.version;
    }
    /**小于等于*/
    public boolean le(ServletVersion otherVersion) {
        return this.version <= otherVersion.version;
    }
}