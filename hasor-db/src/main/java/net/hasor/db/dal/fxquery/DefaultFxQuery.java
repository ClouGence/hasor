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
package net.hasor.db.dal.fxquery;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import ognl.Ognl;
import ognl.OgnlContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 本处理器，兼容 #{...}、${...} 两种写法。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public class DefaultFxQuery extends HashMap<Class<?>, Object> implements Cloneable, FxQuery {
    private final StringBuilder           queryStringOri  = new StringBuilder("");
    private final List<Object>            queryStringPlan = new LinkedList<>();
    private final List<String>            paramEl         = new LinkedList<>();
    private       boolean                 havePlaceholder = false;
    private final AtomicReference<Object> tempObject      = new AtomicReference<>();
    private final Supplier<Object>        objectSupplier  = tempObject::get;

    /** 插入一个字符串 */
    public void insertString(String append) {
        this.queryStringOri.insert(0, append);
        this.queryStringPlan.add(0, append);
    }

    /** 追加一个字符串 */
    public void appendString(String append) {
        this.queryStringOri.append(append);
        if (!this.queryStringPlan.isEmpty()) {
            Object ss = this.queryStringPlan.get(this.queryStringPlan.size() - 1);
            if (ss instanceof StringBuilder) {
                ((StringBuilder) ss).append(append);
                return;
            }
        }
        this.queryStringPlan.add(new StringBuilder(append));
    }

    /** 插入一个 SQL 参数，最终这个参数会通过 PreparedStatement 形式传递。 */
    public void insertValueExpr(String exprString) {
        this.queryStringOri.insert(0, "#{" + exprString + "}");
        this.queryStringPlan.add("?");
        this.paramEl.add(exprString);
    }

    /** 添加一个 SQL 参数，最终这个参数会通过 PreparedStatement 形式传递。 */
    public void appendValueExpr(String exprString) {
        this.queryStringOri.append("#{" + exprString + "}");
        this.queryStringPlan.add("?");
        this.paramEl.add(exprString);
    }

    /** 插入一个动态字符串，动态字符串是指字符串本身内容需要经过表达式计算之后才知道。 */
    public void insertPlaceholderExpr(String exprString) {
        this.queryStringOri.insert(0, "${" + exprString + "}");
        this.queryStringPlan.add(0, new EvalCharSequence(exprString, this.objectSupplier));
        this.havePlaceholder = true;
    }

    /** 追加一个动态字符串，动态字符串是指字符串本身内容需要经过表达式计算之后才知道。 */
    public void appendPlaceholderExpr(String exprString) {
        this.queryStringOri.append("${" + exprString + "}");
        this.queryStringPlan.add(new EvalCharSequence(exprString, this.objectSupplier));
        this.havePlaceholder = true;
    }

    /** 是否包含替换占位符，如果包含替换占位符那么不能使用批量模式 */
    public boolean isHavePlaceholder() {
        return this.havePlaceholder;
    }

    public StringBuilder getOriSqlString() {
        return this.queryStringOri;
    }

    public String buildQueryString(Object context) {
        try {
            this.tempObject.set(context);
            return StringUtils.join(this.queryStringPlan.toArray());
        } finally {
            this.tempObject.set(null);
        }
    }

    public List<Object> buildParameterSource(Object context) {
        return this.paramEl.stream().map(exprString -> {
            return evalOgnl(exprString, context);
        }).collect(Collectors.toList());
    }

    @Override
    public <T> T attach(Class<? extends T> attach, T attachValue) {
        return (T) super.put(attach, attachValue);
    }

    @Override
    public <T> T attach(Class<? extends T> attach) {
        return (T) super.get(attach);
    }

    private static class EvalCharSequence {
        private final String           exprString;
        private final Supplier<Object> exprContext;

        public EvalCharSequence(String exprString, Supplier<Object> exprContext) {
            this.exprString = exprString;
            this.exprContext = exprContext;
        }

        @Override
        public String toString() {
            return String.valueOf(evalOgnl(this.exprString, this.exprContext.get()));
        }
    }

    public static Object evalOgnl(String exprString, Object root) {
        try {
            OgnlContext context = new OgnlContext(null, null, new DefaultMemberAccess(true));
            return Ognl.getValue(exprString, context, root);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }

    public static FxQuery analysisSQL(String fragmentString) {
        final DefaultFxQuery fxSql = new DefaultFxQuery();
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
    public FxQuery clone() {
        return DefaultFxQuery.analysisSQL(this.queryStringOri.toString());
    }
}
