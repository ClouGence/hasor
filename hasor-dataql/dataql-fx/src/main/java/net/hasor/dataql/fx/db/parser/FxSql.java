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
package net.hasor.dataql.fx.db.parser;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import ognl.Ognl;
import ognl.OgnlContext;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * SQL 文本处理器，兼容 #{...}、${...} 两种写法。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public class FxSql implements Cloneable {
    private StringBuilder           sqlStringOri    = new StringBuilder("");
    private List<Object>            sqlStringPlan   = new LinkedList<>();
    private List<String>            paramEl         = new LinkedList<>();
    private boolean                 havePlaceholder = false;
    private AtomicReference<Object> tempObject      = new AtomicReference<>();
    private Supplier<Object>        objectSupplier  = () -> {
        return tempObject.get();
    };

    /** 插入一个字符串 */
    public void insertString(String append) {
        this.sqlStringOri.insert(0, append);
        this.sqlStringPlan.add(0, append);
    }

    /** 追加一个字符串 */
    public void appendString(String append) {
        this.sqlStringOri.append(append);
        if (!this.sqlStringPlan.isEmpty()) {
            Object ss = this.sqlStringPlan.get(this.sqlStringPlan.size() - 1);
            if (ss instanceof StringBuilder) {
                ((StringBuilder) ss).append(append);
                return;
            }
        }
        this.sqlStringPlan.add(new StringBuilder(append));
    }

    /** 插入一个 SQL 参数，最终这个参数会通过 PreparedStatement 形式传递。 */
    public void insertValueExpr(String exprString) {
        this.sqlStringOri.insert(0, "#{" + exprString + "}");
        this.sqlStringPlan.add("?");
        this.paramEl.add(exprString);
    }

    /** 添加一个 SQL 参数，最终这个参数会通过 PreparedStatement 形式传递。 */
    public void appendValueExpr(String exprString) {
        this.sqlStringOri.append("#{" + exprString + "}");
        this.sqlStringPlan.add("?");
        this.paramEl.add(exprString);
    }

    /** 插入一个动态字符串，动态字符串是指字符串本身内容需要经过表达式计算之后才知道。 */
    public void insertPlaceholderExpr(String exprString) {
        this.sqlStringOri.insert(0, "${" + exprString + "}");
        this.sqlStringPlan.add(0, new EvalCharSequence(exprString, this.objectSupplier));
        this.havePlaceholder = true;
    }

    /** 追加一个动态字符串，动态字符串是指字符串本身内容需要经过表达式计算之后才知道。 */
    public void appendPlaceholderExpr(String exprString) {
        this.sqlStringOri.append("${" + exprString + "}");
        this.sqlStringPlan.add(new EvalCharSequence(exprString, this.objectSupplier));
        this.havePlaceholder = true;
    }

    /** 是否包含替换占位符，如果包含替换占位符那么不能使用批量模式 */
    public boolean isHavePlaceholder() {
        return this.havePlaceholder;
    }

    public StringBuilder getOriSqlString() {
        return this.sqlStringOri;
    }

    public String buildSqlString(Object context) {
        try {
            this.tempObject.set(context);
            return StringUtils.join(this.sqlStringPlan.toArray());
        } finally {
            this.tempObject.set(null);
        }
    }

    public List<Object> buildParameterSource(Object context) {
        return this.paramEl.stream().map(exprString -> {
            return evalOgnl(exprString, context);
        }).collect(Collectors.toList());
    }

    private static class EvalCharSequence {
        private String           exprString;
        private Supplier<Object> exprContext;

        public EvalCharSequence(String exprString, Supplier<Object> exprContext) {
            this.exprString = exprString;
            this.exprContext = exprContext;
        }

        @Override
        public String toString() {
            return String.valueOf(evalOgnl(this.exprString, this.exprContext.get()));
        }
    }

    private static Object evalOgnl(String exprString, Object root) {
        try {
            OgnlContext context = new OgnlContext(null, null, new DefaultMemberAccess(true));
            return Ognl.getValue(exprString, context, root);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    public static FxSql analysisSQL(String fragmentString) {
        final FxSql fxSql = new FxSql();
        final String result = new GenericTokenParser(new String[] { "#{", "${" }, "}", (builder, token, content) -> {
            fxSql.appendString(builder.toString());
            if (token.equalsIgnoreCase("${")) {
                fxSql.appendPlaceholderExpr(content);
            }
            if (token.equalsIgnoreCase("#{")) {
                fxSql.appendValueExpr(content);
            }
            builder.delete(0, builder.length());
            return "";
        }).parse(fragmentString);
        fxSql.appendString(result);
        return fxSql;
    }

    @Override
    public FxSql clone() {
        return FxSql.analysisSQL(this.sqlStringOri.toString());
    }
}