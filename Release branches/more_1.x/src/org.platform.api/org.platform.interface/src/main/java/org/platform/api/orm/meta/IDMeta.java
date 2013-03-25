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
package org.platform.api.orm.meta;
/**
 * ID属性
 * @version : 2013-1-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class IDMeta extends AttMeta {
    /**主键生成策略*/
    private KeyGeneratorEnum keyGenerator     = KeyGeneratorEnum.UUIDString;
    /**主键生成器工具类，当keyGenerator为自定义时才有效*/
    private String           keyGeneratorType = "";
    //
    //
    //
    public KeyGeneratorEnum getKeyGenerator() {
        return keyGenerator;
    }
    public void setKeyGenerator(KeyGeneratorEnum keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
    public String getKeyGeneratorType() {
        return keyGeneratorType;
    }
    public void setKeyGeneratorType(String keyGeneratorType) {
        this.keyGeneratorType = keyGeneratorType;
    }
}