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
package net.hasor.db.mapping.resolve;
import net.hasor.db.metadata.CaseSensitivityType;

/**
 * 工具类
 * @version : 2021-06-21
 * @author 赵永春 (zyc@hasor.net)
 */
public class AbstractResolveTableMapping {
    protected CaseSensitivityType caseSensitivity(CaseSensitivityType check, CaseSensitivityType defaultType) {
        return (check == null) ? defaultType : check;
    }

    protected String formatCaseSensitivity(String dataString, CaseSensitivityType sensitivityType) {
        if (sensitivityType == null || dataString == null) {
            return dataString;
        }
        switch (sensitivityType) {
            case Lower: {
                return dataString.toLowerCase();
            }
            case Upper: {
                return dataString.toUpperCase();
            }
            default: {
                return dataString;
            }
        }
    }
}
