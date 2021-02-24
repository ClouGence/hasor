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
package net.hasor.dataql.parser.ast.value;
import net.hasor.dataql.Hints;
import net.hasor.dataql.parser.ast.*;
import net.hasor.dataql.parser.ast.token.StringToken;
import net.hasor.dataql.parser.location.BlockLocation;
import net.hasor.utils.StringUtils;

import java.io.IOException;

/**
 * 函数调用 - 之所以是 Variable 是由于 FunctionCall 的最终结果是 函数调用的返回值。而返回值是属于 Variable 的
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class NameRouteVariable extends BlockLocation implements Variable, RouteVariable {
    private final RouteVariable parent;
    private final StringToken   name;

    public NameRouteVariable(RouteVariable parent, StringToken name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public RouteVariable getParent() {
        return this.parent;
    }

    public StringToken getName() {
        return name;
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

    @Override
    public void doFormat(int depth, Hints formatOption, FormatWriter writer) throws IOException {
        EnterRouteVariable.RouteType routeType = null;
        EnterRouteVariable.SpecialType specialType = EnterRouteVariable.SpecialType.Special_A;
        if (this.parent instanceof EnterRouteVariable) {
            routeType = ((EnterRouteVariable) parent).getRouteType();
            specialType = ((EnterRouteVariable) parent).getSpecialType();
            if (specialType == null) {
                specialType = EnterRouteVariable.SpecialType.Special_A;
            }
        }
        //
        if (EnterRouteVariable.RouteType.Params == routeType) {
            writer.write(specialType.getCode() + "{");
        }
        //
        this.parent.doFormat(depth, formatOption, writer);
        if (this.parent instanceof EnterRouteVariable) {
            if (StringUtils.isBlank(this.name.getValue())) {
                EnterRouteVariable.SpecialType special = ((EnterRouteVariable) this.parent).getSpecialType();
                if (special != EnterRouteVariable.SpecialType.Special_A) {
                    writer.write(((EnterRouteVariable) this.parent).getSpecialType().getCode());
                }
            } else {
                if (EnterRouteVariable.RouteType.Params != routeType && EnterRouteVariable.SpecialType.Special_A != specialType) {
                    writer.write(specialType.getCode());
                }
                writer.write(this.name.getValue());
            }
        } else {
            if (this.parent instanceof NameRouteVariable && StringUtils.isBlank(((NameRouteVariable) this.parent).name.getValue())) {
                writer.write(this.name.getValue());
            } else {
                writer.write("." + this.name.getValue());
            }
        }
        //
        if (EnterRouteVariable.RouteType.Params == routeType) {
            writer.write("}");
        }
    }
}
