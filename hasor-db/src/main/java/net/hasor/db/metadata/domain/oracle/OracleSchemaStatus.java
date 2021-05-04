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
/**
 * Oracle 账号 状态
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public enum OracleSchemaStatus {
    Open("OPEN", false, false, false, false),
    Expired("EXPIRED", false, true, false, false),
    ExpiredGrace("EXPIRED(GRACE)", false, true, true, false),
    LockedTimed("LOCKED(TIMED)", true, false, false, true),
    Locked("LOCKED", true, false, false, false),
    ExpiredLockedTimed("EXPIRED & LOCKED(TIMED)", true, true, false, true),
    ExpiredGraceLockedTimed("EXPIRED(GRACE) & LOCKED(TIMED)", true, true, true, true),
    ExpiredLocked("EXPIRED & LOCKED", true, true, false, false),
    ExpiredGraceLocked("EXPIRED(GRACE) & LOCKED", true, true, true, false),
    ;
    private final String  typeName;
    private final boolean lock;
    private final boolean expired;
    private final boolean grace;
    private final boolean timed;

    OracleSchemaStatus(String typeName, boolean lock, boolean expired, boolean grace, boolean timed) {
        this.typeName = typeName;
        this.lock = lock;
        this.expired = expired;
        this.grace = grace;
        this.timed = timed;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public boolean isLock() {
        return this.lock;
    }

    public boolean isExpired() {
        return this.expired;
    }

    public boolean isGrace() {
        return this.grace;
    }

    public boolean isTimed() {
        return timed;
    }

    public static OracleSchemaStatus valueOfCode(String code) {
        for (OracleSchemaStatus tableType : OracleSchemaStatus.values()) {
            if (tableType.typeName.equals(code)) {
                return tableType;
            }
        }
        return null;
    }
}
