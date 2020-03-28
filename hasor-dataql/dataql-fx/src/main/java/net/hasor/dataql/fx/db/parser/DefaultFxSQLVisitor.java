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
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

/**
 * This class provides an empty implementation of {@link FxSQLParserVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return type.
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-28
 */
public class DefaultFxSQLVisitor<T> extends AbstractParseTreeVisitor<T> implements FxSQLParserVisitor<T> {
    private FxSql fxSql = new FxSql();

    @Override
    public T visitRootInstSet(FxSQLParser.RootInstSetContext ctx) {
        visitChildren(ctx);
        return (T) fxSql;
    }

    @Override
    public T visitOgnlExpr(FxSQLParser.OgnlExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitExpr(FxSQLParser.ExprContext ctx) {
        List<TerminalNode> ognlHafStr = ctx.OGNL_HAFSTR();
        StringBuilder ognlString = new StringBuilder();
        if (ognlHafStr != null) {
            ognlHafStr.forEach(terminalNode -> {
                ognlString.append(terminalNode.getText());
            });
        }
        //
        TerminalNode oriNode = ctx.ORI_BEGIN();
        TerminalNode safNode = ctx.SAF_BEGIN();
        if (oriNode != null) {
            this.fxSql.appendPlaceholderExpr(ognlString.toString());
        }
        if (safNode != null) {
            this.fxSql.appendValueExpr(ognlString.toString());
        }
        return null;
    }

    @Override
    public T visitCharA(FxSQLParser.CharAContext ctx) {
        return visitChar(ctx.CHAR());
    }

    @Override
    public T visitCharB(FxSQLParser.CharBContext ctx) {
        return visitChar(ctx.CHAR());
    }

    private T visitChar(List<TerminalNode> chars) {
        StringBuilder tmpString = new StringBuilder();
        if (chars != null) {
            chars.forEach(terminalNode -> {
                tmpString.append(terminalNode.getText());
            });
        }
        fxSql.appendString(tmpString.toString());
        return null;
    }
}