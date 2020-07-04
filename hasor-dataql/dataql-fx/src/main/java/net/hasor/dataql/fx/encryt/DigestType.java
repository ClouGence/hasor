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
package net.hasor.dataql.fx.encryt;
/**
 * 摘要算法类型枚举
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-31
 */
public enum DigestType {
    MD5("MD5"),         //
    SHA("SHA"),         //
    SHA1("SHA1"),       //
    SHA256("SHA256"),   //
    SHA512("SHA512");   //
    private final String digestDesc;

    private DigestType(String digestDesc) {
        this.digestDesc = digestDesc;
    }

    public static DigestType formString(String digestString) {
        for (DigestType digestType : DigestType.values()) {
            if (digestType.name().equalsIgnoreCase(digestString)) {
                return digestType;
            }
        }
        return null;
    }

    public String getDigestDesc() {
        return digestDesc;
    }
}