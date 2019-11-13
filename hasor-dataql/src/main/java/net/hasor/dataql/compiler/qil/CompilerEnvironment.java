package net.hasor.dataql.compiler.qil;
import net.hasor.dataql.compiler.ast.Inst;
import net.hasor.dataql.compiler.ast.expr.*;
import net.hasor.dataql.compiler.ast.inst.*;
import net.hasor.dataql.compiler.ast.value.EnterRouteVariable;
import net.hasor.dataql.compiler.ast.value.NameRouteVariable;
import net.hasor.dataql.compiler.ast.value.PrimitiveVariable;
import net.hasor.dataql.compiler.ast.value.SubscriptRouteVariable;
import net.hasor.dataql.compiler.qil.cc.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CompilerEnvironment {
    private static Map<Class<?>, InstCompiler<?>> typeMappingToInstCompiler = new HashMap<Class<?>, InstCompiler<?>>() {{
        put(RootBlockSet.class, new RootBlockSetInstCompiler());
        put(InstSet.class, new InstSetInstCompiler());
        //
        put(OptionInst.class, new OptionInstCompiler());
        put(ExitInst.class, new ExitInstCompiler());
        put(ReturnInst.class, new ReturnInstCompiler());
        put(ThrowInst.class, new ThrowInstCompiler());
        put(VarInst.class, new VarInstCompiler());
        put(SwitchInst.class, new SwitchInstCompiler());
        //
        put(AtomExpression.class, new AtomExprInstCompiler());
        put(UnaryExpression.class, new UnaryExprInstCompiler());
        put(DyadicExpression.class, new DyadicExprInstCompiler());
        put(TernaryExpression.class, new TernaryExprInstCompiler());
        put(PrivilegeExpression.class, new PrivilegeExprInstCompiler());
        //
        put(PrimitiveVariable.class, new PrimitiveVariableInstCompiler());
        //
        put(SubscriptRouteVariable.class, new SubscriptRouteVariableInstCompiler());
        put(NameRouteVariable.class, new NameRouteVariableInstCompiler());
        put(EnterRouteVariable.class, new EnterRouteVariableInstCompiler());
        //
    }};

    public <T extends Inst> InstCompiler<T> findInstCompilerByType(Class<T> instType) {
        return (InstCompiler<T>) Objects.requireNonNull(typeMappingToInstCompiler.get(instType), "not found " + instType.getName() + " InstCompiler.");
    }
}