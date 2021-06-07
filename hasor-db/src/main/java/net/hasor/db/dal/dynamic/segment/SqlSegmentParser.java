/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.dal.dynamic.segment;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.rule.ParameterSqlBuildRule;
import net.hasor.db.dal.dynamic.tokens.GenericTokenParser;
import net.hasor.utils.StringUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;

import java.util.Map;

/**
 * DynamicSql 解析器，仅支持：@{}、#{}、${} 三种写法。
 * @version : 2021-05-19
 * @author 赵永春 (zyc@hasor.net)
 */
public class SqlSegmentParser {
    public static DynamicSql analysisSQL(String fragmentString) {
        final DefaultSqlSegment fxSql = new DefaultSqlSegment();
        final String result = new GenericTokenParser(new String[] { "@{", "#{", "${" }, "}", (builder, token, content) -> {
            fxSql.appendString(builder.toString());
            if (token.equalsIgnoreCase("@{")) {
                parserRule(fxSql, content);
            }
            if (token.equalsIgnoreCase("${")) {
                parserPlaceholder(fxSql, content);
            }
            if (token.equalsIgnoreCase("#{")) {
                parserValue(fxSql, content);
            }
            builder.delete(0, builder.length());
            return "";
        }).parse(fragmentString);
        fxSql.appendString(result);
        return fxSql;
    }

    private static void parserPlaceholder(DefaultSqlSegment fxQuery, String content) {
        fxQuery.appendPlaceholderExpr(content);
    }

    private static void parserRule(DefaultSqlSegment fxQuery, String content) {
        String[] ruleData = content.split(",");
        if (ruleData.length > 3 || ruleData.length == 0) {
            throw new IllegalArgumentException("analysisSQL failed, format error -> '@{ruleName [, activateExpr [, exprString]]}'");
        }
        if (StringUtils.isBlank(ruleData[0])) {
            throw new IllegalArgumentException("analysisSQL failed, Rule name not specified.");
        }
        //
        if (ruleData.length > 1 && StringUtils.isBlank(ruleData[1])) {
            throw new IllegalArgumentException("analysisSQL failed, activation condition not specified.");
        }
        //
        String ruleName = (ruleData.length > 0) ? ruleData[0].trim() : null;
        String activateExpr = (ruleData.length > 1) ? ruleData[1].trim() : null;
        String exprString = (ruleData.length > 2) ? ruleData[2].trim() : null;
        fxQuery.appendRuleExpr(ruleName, activateExpr, exprString);
    }

    private static void parserValue(DefaultSqlSegment fxQuery, String content) {
        String[] valueData = content.split(",");
        if (valueData.length > 5 || valueData.length == 0) {
            throw new IllegalArgumentException("analysisSQL failed, format error -> '#{valueExpr [,mode= IN|OUT|INOUT] [,jdbcType=INT] [,javaType=java.lang.String] [,typeHandler=YouTypeHandlerClassName]}'");
        }
        //
        Map<String, String> exprMap = new LinkedCaseInsensitiveMap<>();
        for (int i = 1; i < valueData.length; i++) {
            String data = valueData[i];
            String[] kv = data.split("=");
            if (kv.length != 2) {
                throw new IllegalArgumentException("analysisSQL failed, config must be 'key = value' , '" + content + "' with '" + data + "'");
            }
            if (StringUtils.isNotBlank(kv[0])) {
                exprMap.put(kv[0].trim(), kv[1].trim());
            }
        }
        //
        String exprString = valueData[0];
        String sqlMode = exprMap.get(ParameterSqlBuildRule.CFG_KEY_MODE);
        String jdbcType = exprMap.get(ParameterSqlBuildRule.CFG_KEY_JDBC_TYPE);
        String javaType = exprMap.get(ParameterSqlBuildRule.CFG_KEY_JAVA_TYPE);
        String typeHandler = exprMap.get(ParameterSqlBuildRule.CFG_KEY_HANDLER);
        fxQuery.appendValueExpr(exprString, sqlMode, jdbcType, javaType, typeHandler);
    }
}
