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
package net.hasor.dataql.compiler.qil;
import net.hasor.dataql.Finder;
import net.hasor.dataql.compiler.ast.Inst;
import net.hasor.dataql.compiler.ast.expr.*;
import net.hasor.dataql.compiler.ast.fmt.ListFormat;
import net.hasor.dataql.compiler.ast.fmt.ObjectFormat;
import net.hasor.dataql.compiler.ast.inst.*;
import net.hasor.dataql.compiler.ast.value.*;
import net.hasor.dataql.compiler.qil.cc.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Finder 接口的内部实现，
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-11
 */
public class CompilerEnvironment implements Finder {
    private Finder finder;

    public CompilerEnvironment(Finder finder) {
        this.finder = Objects.requireNonNull(finder, "finder is null.");
    }

    @Override
    public InputStream findResource(String resourceName) throws IOException {
        return this.finder.findResource(resourceName);
    }

    public <T extends Inst> InstCompiler<T> findInstCompilerByType(Class<T> instType) {
        return (InstCompiler<T>) Objects.requireNonNull(typeMappingToInstCompiler.get(instType), "not found " + instType.getName() + " InstCompiler.");
    }

    private static Map<Class<?>, InstCompiler<?>> typeMappingToInstCompiler = new HashMap<Class<?>, InstCompiler<?>>() {{
        //
        put(RootBlockSet.class, new RootBlockSetInstCompiler());
        put(InstSet.class, new InstSetInstCompiler());
        //
        put(HintInst.class, new HintInstCompiler());
        put(ImportInst.class, new ImportInstCompiler());
        put(ExitInst.class, new ExitInstCompiler());
        put(ReturnInst.class, new ReturnInstCompiler());
        put(ThrowInst.class, new ThrowInstCompiler());
        put(VarInst.class, new VarInstCompiler());
        put(RunInst.class, new RunInstCompiler());
        put(SwitchInst.class, new SwitchInstCompiler());
        //
        put(AtomExpression.class, new AtomExprInstCompiler());
        put(UnaryExpression.class, new UnaryExprInstCompiler());
        put(DyadicExpression.class, new DyadicExprInstCompiler());
        put(TernaryExpression.class, new TernaryExprInstCompiler());
        put(PrivilegeExpression.class, new PrivilegeExprInstCompiler());
        //
        put(PrimitiveVariable.class, new PrimitiveVariableInstCompiler());
        put(LambdaVariable.class, new LambdaVariableInstCompiler());
        put(ListVariable.class, new ListVariableInstCompiler());
        put(ObjectVariable.class, new ObjectVariableInstCompiler());
        //
        put(ObjectFormat.class, new ObjectFormatInstCompiler());
        put(ListFormat.class, new ListFormatInstCompiler());
        put(FragmentVariable.class, new FragmentVariableInstCompiler());
        //
        put(SubscriptRouteVariable.class, new SubscriptRouteVariableInstCompiler());
        put(NameRouteVariable.class, new NameRouteVariableInstCompiler());
        put(EnterRouteVariable.class, new EnterRouteVariableInstCompiler());
        put(FunCallRouteVariable.class, new FunCallRouteVariableInstCompiler());
    }};
}
