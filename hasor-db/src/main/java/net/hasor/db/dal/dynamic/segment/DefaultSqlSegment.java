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
package net.hasor.db.dal.dynamic.segment;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.dynamic.rule.ParameterSqlBuildRule;
import net.hasor.db.dal.dynamic.rule.SqlBuildRule;
import net.hasor.db.dal.dynamic.rule.TextSqlBuildRule;
import net.hasor.utils.StringUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.hasor.db.dal.dynamic.ognl.OgnlUtils.evalOgnl;
import static net.hasor.db.dal.dynamic.rule.ParameterSqlBuildRule.*;

/**
 * 本处理器，兼容 @{...}、#{...}、${...} 三种写法。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public class DefaultSqlSegment implements Cloneable, DynamicSql {
    private final StringBuilder   queryStringOri  = new StringBuilder("");
    private final List<FxSegment> queryStringPlan = new LinkedList<>();
    private       boolean         havePlaceholder = false;

    /** 追加 字符串 */
    public void appendString(String append) {
        this.queryStringOri.append(append);
        if (!this.queryStringPlan.isEmpty()) {
            Object ss = this.queryStringPlan.get(this.queryStringPlan.size() - 1);
            if (ss instanceof TextFxSegment) {
                ((TextFxSegment) ss).append(append);
                return;
            }
        }
        this.queryStringPlan.add(new TextFxSegment(append));
    }

    /** 追加 注入语句 */
    public void appendPlaceholderExpr(String exprString) {
        this.queryStringOri.append("${" + exprString + "}");
        this.queryStringPlan.add(new PlaceholderFxSegment(exprString));
        this.havePlaceholder = true;
    }

    /** 追加 规则 */
    public void appendRuleExpr(String ruleName, String activateExpr, String exprString) {
        this.queryStringOri.append("@{" + ruleName + ", " + activateExpr + ", " + exprString + "}");
        this.queryStringPlan.add(new RuleFxSegment(ruleName, activateExpr, exprString));
        this.havePlaceholder = true;
    }

    /** 添加一个 SQL 参数，最终这个参数会通过 PreparedStatement 形式传递。 */
    public void appendValueExpr(String exprString, String sqlMode, String jdbcType, String javaType, String typeHandler) {
        this.queryStringOri.append("#{");
        this.queryStringOri.append(exprString);
        if (sqlMode != null) {
            this.queryStringOri.append(", mode=" + sqlMode);
        }
        if (StringUtils.isNotBlank(jdbcType)) {
            this.queryStringOri.append(", jdbcType=" + jdbcType);
        }
        if (StringUtils.isNotBlank(javaType)) {
            this.queryStringOri.append(", javaType=" + javaType);
        }
        if (StringUtils.isNotBlank(typeHandler)) {
            this.queryStringOri.append(", typeHandler=" + typeHandler);
        }
        this.queryStringOri.append("}");
        //
        this.queryStringPlan.add(new ParameterFxSegment(exprString, sqlMode, jdbcType, javaType, typeHandler));
    }

    /** 是否包含替换占位符，如果包含替换占位符那么不能使用批量模式 */
    public boolean isHavePlaceholder() {
        return this.havePlaceholder;
    }

    public String getOriSqlString() {
        return this.queryStringOri.toString();
    }

    @Override
    public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
        for (FxSegment fxSegment : this.queryStringPlan) {
            fxSegment.buildQuery(builderContext, querySqlBuilder);
        }
    }

    @Override
    public DynamicSql clone() {
        DefaultSqlSegment clone = new DefaultSqlSegment();
        clone.queryStringOri.append(this.queryStringOri);
        for (FxSegment fxSegment : this.queryStringPlan) {
            clone.queryStringPlan.add(fxSegment.clone());
        }
        clone.havePlaceholder = this.havePlaceholder;
        return clone;
    }

    public static interface FxSegment extends Cloneable {
        public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException;

        public FxSegment clone();
    }

    protected static class TextFxSegment implements FxSegment {
        private final StringBuilder textString;

        public TextFxSegment(String exprString) {
            this.textString = new StringBuilder(exprString);
        }

        public void append(String append) {
            this.textString.append(append);
        }

        @Override
        public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
            TextSqlBuildRule.INSTANCE.executeRule(builderContext, querySqlBuilder, this.textString.toString(), Collections.emptyMap());
        }

        @Override
        public TextFxSegment clone() {
            return new TextFxSegment(this.textString.toString());
        }

        @Override
        public String toString() {
            return "Text [" + this.textString + "]";
        }
    }

    protected static class PlaceholderFxSegment implements FxSegment {
        private final StringBuilder exprString;

        public PlaceholderFxSegment(String exprString) {
            this.exprString = new StringBuilder(exprString);
        }

        @Override
        public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
            String placeholderQuery = String.valueOf(evalOgnl(this.exprString.toString(), builderContext.getContext()));
            TextSqlBuildRule.INSTANCE.executeRule(builderContext, querySqlBuilder, placeholderQuery, Collections.emptyMap());
        }

        @Override
        public PlaceholderFxSegment clone() {
            return new PlaceholderFxSegment(this.exprString.toString());
        }

        @Override
        public String toString() {
            return "Placeholder [" + this.exprString + "]";
        }
    }

    protected static class RuleFxSegment implements FxSegment {
        private final String ruleName;
        private final String activateExpr;
        private final String ruleValue;

        public RuleFxSegment(String ruleName, String activateExpr, String ruleValue) {
            this.ruleName = ruleName;
            this.activateExpr = activateExpr;
            this.ruleValue = ruleValue;
        }

        @Override
        public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
            boolean activate = Boolean.TRUE.equals(evalOgnl(this.activateExpr, builderContext.getContext()));
            if (activate) {
                SqlBuildRule ruleByName = builderContext.getRuleRegistry().findByName(this.ruleName);
                if (ruleByName == null) {
                    throw new UnsupportedOperationException("rule `" + this.ruleName + "` Unsupported.");
                }
                ruleByName.executeRule(builderContext, querySqlBuilder, this.ruleValue, Collections.emptyMap());
            }
        }

        @Override
        public RuleFxSegment clone() {
            return new RuleFxSegment(this.ruleName, this.activateExpr, this.ruleValue);
        }

        @Override
        public String toString() {
            return "Rule [" + this.ruleName + ", body=" + this.ruleValue + "]";
        }
    }

    protected static class ParameterFxSegment implements FxSegment {
        private final String              exprString;
        private final Map<String, String> config;

        public ParameterFxSegment(String exprString, String sqlMode, String jdbcType, String javaType, String typeHandler) {
            this.exprString = exprString;
            this.config = new LinkedCaseInsensitiveMap<String>() {{
                put(CFG_KEY_MODE, sqlMode);
                put(CFG_KEY_JDBC_TYPE, jdbcType);
                put(CFG_KEY_JAVA_TYPE, javaType);
                put(CFG_KEY_HANDLER, typeHandler);
            }};
        }

        @Override
        public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
            ParameterSqlBuildRule.INSTANCE.executeRule(builderContext, querySqlBuilder, this.exprString, this.config);
        }

        @Override
        public ParameterFxSegment clone() {
            return new ParameterFxSegment(this.exprString, this.config.get(CFG_KEY_MODE), this.config.get(CFG_KEY_JDBC_TYPE), this.config.get(CFG_KEY_JAVA_TYPE), this.config.get(CFG_KEY_HANDLER));
        }

        @Override
        public String toString() {
            return "Parameter [" + this.exprString + "]";
        }
    }
}
