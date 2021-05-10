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
package net.hasor.db.metadata.domain.oracle;
import net.hasor.db.metadata.TableType;

/**
 * Oracle 索引类型
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public enum OracleIndexType implements TableType {
    Lob("LOB"),
    Normal("NORMAL"),
    NormalRev("NORMAL/REV"),
    Bitmap("BITMAP"),
    FunctionBasedNormal("FUNCTION-BASED NORMAL"),
    FunctionBasedNormalRev("FUNCTION-BASED NORMAL/REV"),
    FunctionBasedBitmap("FUNCTION-BASED BITMAP"),
    FunctionBasedDomain("FUNCTION-BASED DOMAIN"),
    Cluster("CLUSTER"),
    IotTop("IOT - TOP"),
    Domain("DOMAIN"),
    ;
    private final String typeName;

    OracleIndexType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static OracleIndexType valueOfCode(String code) {
        for (OracleIndexType tableType : OracleIndexType.values()) {
            if (tableType.typeName.equals(code)) {
                return tableType;
            }
        }
        return null;
    }
}
