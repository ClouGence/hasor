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
package net.hasor.dataql.compiler.ast.value;
import net.hasor.dataql.Option;
import net.hasor.dataql.compiler.ast.AstVisitor;
import net.hasor.dataql.compiler.ast.FormatWriter;
import net.hasor.dataql.compiler.ast.InstVisitorContext;
import net.hasor.dataql.compiler.ast.RouteVariable;

import java.io.IOException;

/**
 * 路由的入口，一切路由操作都要有一个入口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class EnterRouteVariable implements RouteVariable {
    public static enum RouteType {
        /** 一般路由(含升级的一般路由)，只从环境栈顶上获取数据 */
        Normal(),
        /** 自定义取值 */
        Special(),
        ;
    }

    public static enum SpecialType {
        Special_A("#"),  // 特殊路由1，自定义
        Special_B("$"),  // 特殊路由2，自定义
        Special_C("@"),  // 特殊路由3，自定义
        ;
        private String code;

        SpecialType(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    private RouteType   routeType;
    private SpecialType specialType;

    public EnterRouteVariable(RouteType routeType, SpecialType specialType) {
        this.routeType = routeType;
        this.specialType = specialType;
    }

    @Override
    public RouteVariable getParent() {
        return null;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public SpecialType getSpecialType() {
        return this.specialType;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
            }
        });
    }

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
    }
}