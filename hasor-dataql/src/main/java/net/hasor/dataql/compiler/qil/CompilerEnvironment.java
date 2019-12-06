package net.hasor.dataql.compiler.qil;
import net.hasor.dataql.Finder;
import net.hasor.dataql.compiler.ast.Inst;
import net.hasor.dataql.compiler.ast.expr.*;
import net.hasor.dataql.compiler.ast.fmt.ListFormat;
import net.hasor.dataql.compiler.ast.fmt.ObjectFormat;
import net.hasor.dataql.compiler.ast.inst.*;
import net.hasor.dataql.compiler.ast.value.*;
import net.hasor.dataql.compiler.qil.cc.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CompilerEnvironment implements Finder {
    private Finder finder;

    public CompilerEnvironment(Finder finder) {
        this.finder = Objects.requireNonNull(finder, "finder is null.");
    }

    @Override
    public InputStream findResource(String resourceName) {
        return this.finder.findResource(resourceName);
    }

    @Override
    public Object findBean(String beanName) {
        return this.finder.findBean(beanName);
    }

    public <T extends Inst> InstCompiler<T> findInstCompilerByType(Class<T> instType) {
        return (InstCompiler<T>) Objects.requireNonNull(typeMappingToInstCompiler.get(instType), "not found " + instType.getName() + " InstCompiler.");
    }

    private static Map<Class<?>, InstCompiler<?>> typeMappingToInstCompiler = new HashMap<Class<?>, InstCompiler<?>>() {{
        //
        put(RootBlockSet.class, new RootBlockSetInstCompiler());
        put(InstSet.class, new InstSetInstCompiler());
        //
        put(OptionInst.class, new OptionInstCompiler());
        put(ImportInst.class, new ImportInstCompiler());
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
        put(LambdaVariable.class, new LambdaVariableInstCompiler());
        put(ListVariable.class, new ListVariableInstCompiler());
        put(ObjectVariable.class, new ObjectVariableInstCompiler());
        //
        put(ObjectFormat.class, new ObjectFormatInstCompiler());
        put(ListFormat.class, new ListFormatInstCompiler());
        //
        put(SubscriptRouteVariable.class, new SubscriptRouteVariableInstCompiler());
        put(NameRouteVariable.class, new NameRouteVariableInstCompiler());
        put(EnterRouteVariable.class, new EnterRouteVariableInstCompiler());
        put(FunCallRouteVariable.class, new FunCallRouteVariableInstCompiler());
    }};
}