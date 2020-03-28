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
import net.hasor.db.jdbc.SqlParameterSource;
import net.hasor.db.jdbc.paramer.MapSqlParameterSource;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import ognl.Ognl;
import ognl.OgnlContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * SQL 文本处理器，兼容 #{...}、${...} 两种写法。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public class FxSql implements Supplier<Object> {
    private List<Object>        sqlStringPlan   = new ArrayList<>();
    private Map<String, String> paramMapping    = new HashMap<>();
    private AtomicInteger       atomicInteger   = new AtomicInteger();
    private boolean             havePlaceholder = false;
    private Object              tempObject      = null;

    /** 追加一个字符串 */
    public void appendString(String append) {
        this.sqlStringPlan.add(append);
    }

    /** 添加一个 SQL 参数，最终这个参数会通过 PreparedStatement 形式传递。 */
    public void appendValueExpr(String ognlExpr) {
        String paramKey = "param_" + this.atomicInteger.incrementAndGet();
        this.sqlStringPlan.add(":" + paramKey);
        this.paramMapping.put(paramKey, ognlExpr);
    }

    /** 追加一个动态字符串，动态字符串是指字符串本身内容需要经过表达式计算之后才知道。 */
    public void appendPlaceholderExpr(String ognlExpr) {
        this.sqlStringPlan.add(new EvalCharSequence(ognlExpr, this));
        this.havePlaceholder = true;
    }

    @Override
    public Object get() {
        return this.tempObject;
    }

    /** 是否包含替换占位符，如果包含替换占位符那么不能使用批量模式 */
    public boolean isHavePlaceholder() {
        return this.havePlaceholder;
    }

    public String buildSqlString(Object context) {
        try {
            this.tempObject = context;
            return StringUtils.join(this.sqlStringPlan.toArray());
        } finally {
            this.tempObject = null;
        }
    }

    public SqlParameterSource buildParameterSource(Object context) {
        Map<String, Supplier<?>> parameterMap = new HashMap<>();
        this.paramMapping.forEach((paramKey, ognlExpr) -> {
            parameterMap.put(paramKey, () -> {
                return evalOgnl(ognlExpr, context);
            });
        });
        return new MapSqlParameterSource(parameterMap);
    }

    private static class EvalCharSequence {
        private String           ognlExpr;
        private Supplier<Object> ognlContext;

        public EvalCharSequence(String ognlExpr, Supplier<Object> ognlContext) {
            this.ognlExpr = ognlExpr;
            this.ognlContext = ognlContext;
        }

        @Override
        public String toString() {
            return String.valueOf(evalOgnl(this.ognlExpr, this.ognlContext.get()));
        }
    }

    private static Object evalOgnl(String ognlExpr, Object root) {
        try {
            OgnlContext context = new OgnlContext(null, null, new DefaultMemberAccess(true));
            return Ognl.getValue(ognlExpr, context, root);
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}