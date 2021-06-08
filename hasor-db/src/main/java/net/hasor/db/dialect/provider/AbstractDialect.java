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
package net.hasor.db.dialect.provider;
import net.hasor.db.dialect.ConditionSqlDialect;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.TableDef;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 公共 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractDialect implements SqlDialect, ConditionSqlDialect {
    private static final Logger      logger = LoggerFactory.getLogger(AbstractDialect.class);
    private              Set<String> keyWords;

    @Override
    public final Set<String> keywords() {
        if (keyWords == null) {
            keyWords = new HashSet<>();
            String keyWordsResource = keyWordsResource();
            if (StringUtils.isBlank(keyWordsResource)) {
                return keyWords;
            }
            try {
                List<String> strings = IOUtils.readLines(ResourcesUtils.getResourceAsStream(keyWordsResource), StandardCharsets.UTF_8);
                for (String term : strings) {
                    term = term.trim().toUpperCase();
                    if (!StringUtils.isBlank(term) && term.charAt(0) != '#') {
                        keyWords.add(term);
                    }
                }
            } catch (Exception e) {
                logger.error("load " + this.keywords() + ".keywords failed." + e.getMessage(), e);
            }
        }
        return keyWords;
    }

    protected String keyWordsResource() {
        return null;
    }

    @Override
    public String tableName(boolean useQualifier, TableDef tableDef) {
        if (StringUtils.isBlank(tableDef.getSchema())) {
            return fmtName(useQualifier, tableDef.getTable());
        } else {
            return fmtName(useQualifier, tableDef.getSchema()) + "." + fmtName(useQualifier, tableDef.getTable());
        }
    }

    @Override
    public String columnName(boolean useQualifier, TableDef tableDef, ColumnDef columnDef) {
        return fmtName(useQualifier, columnDef.getName());
    }

    protected String fmtName(boolean useQualifier, String fmtString) {
        if (this.keywords().contains(fmtString.toUpperCase())) {
            useQualifier = true;
        }
        String leftQualifier = useQualifier ? leftQualifier() : "";
        String rightQualifier = useQualifier ? rightQualifier() : "";
        return leftQualifier + fmtString + rightQualifier;
    }

    protected String defaultQualifier() {
        return "";
    }

    public String leftQualifier() {
        return this.defaultQualifier();
    }

    public String rightQualifier() {
        return this.defaultQualifier();
    }
}
