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
package net.hasor.dataql.compiler.qil.cc;
import net.hasor.dataql.compiler.CompilerException;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.RouteType;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable.SpecialType;
import net.hasor.dataql.compiler.qil.CompilerContext;
import net.hasor.dataql.compiler.qil.InstCompiler;
import net.hasor.dataql.compiler.qil.InstQueue;

/**
 * 路由的入口，一切路由操作都要有一个入口
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class EnterRouteVariableInstCompiler implements InstCompiler<EnterRouteVariable> {
    @Override
    public void doCompiler(EnterRouteVariable astInst, InstQueue queue, CompilerContext compilerContext) {
        RouteType routeType = astInst.getRouteType();
        SpecialType specialType = astInst.getSpecialType();
        this.instLocation(queue, astInst);//行号
        //
        // 表达式
        if (routeType == RouteType.Expr) {
            specialType = (specialType == null) ? SpecialType.Special_A : specialType;
            queue.inst(E_LOAD, specialType.getCode());
            return;
        }
        //
        // 程序传参
        if (routeType == RouteType.Params) {
            queue.inst(LOAD_C, specialType.getCode());
            return;
        }
        throw new CompilerException("routeType is null.");
    }
}