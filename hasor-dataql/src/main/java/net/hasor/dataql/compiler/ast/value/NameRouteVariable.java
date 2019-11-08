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
import net.hasor.dataql.compiler.ast.*;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.RouteType;
import net.hasor.dataql.compiler.qil.CompilerStack;
import net.hasor.dataql.compiler.qil.InstQueue;
import net.hasor.dataql.runtime.OptionSet;
import net.hasor.utils.StringUtils;

import java.io.IOException;

/**
 * 函数调用 - 之所以是 Variable 是由于 FunctionCall 的最终结果是 函数调用的返回值。而返回值是属于 Variable 的
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class NameRouteVariable implements Variable, RouteVariable {
    private RouteVariable parent;
    private String        name;

    public NameRouteVariable(RouteVariable parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public RouteVariable getParent() {
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void accept(AstVisitor astVisitor) {
        if (this.parent != null) {
            this.parent.accept(astVisitor);
        }
        astVisitor.visitInst(new InstVisitorContext(this) {
            @Override
            public void visitChildren(AstVisitor astVisitor) {
            }
        });
    }

    private static String ignoreName = NameRouteVariable.class.getName() + "_ignore_routeType";

    @Override
    public void doFormat(int depth, Option formatOption, FormatWriter writer) throws IOException {
        RouteType routeType = RouteType.Context;
        String optValue = (String) formatOption.getOption(ignoreName);
        if (!"true".equals(optValue)) {
            RouteVariable parent = this;
            while (true) {
                if (parent == null) {
                    break;
                }
                if (parent instanceof EnterRouteVariable) {
                    routeType = ((EnterRouteVariable) parent).getRouteType();
                    break;
                }
                parent = parent.getParent();
            }
        }
        //
        if (StringUtils.isNotBlank(routeType.getCode())) {
            writer.write(routeType.getCode() + "{");
        }
        //
        OptionSet optionSet = new OptionSet(formatOption);
        optionSet.setOption(ignoreName, "true");
        this.parent.doFormat(depth, optionSet, writer);
        if (this.parent instanceof EnterRouteVariable) {
            writer.write(this.name);
        } else {
            writer.write("." + this.name);
        }
        //
        if (StringUtils.isNotBlank(routeType.getCode())) {
            writer.write("}");
        }
    }

    @Override
    public void doCompiler(InstQueue queue, CompilerStack stackTree) {
        //
    }
}