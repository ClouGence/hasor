package net.hasor.dataql.runtime.operator;
import net.hasor.core.provider.SingleProvider;
import net.hasor.dataql.runtime.operator.ops.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class OperatorManager implements DyadicOperatorRegistry, UnaryOperatorRegistry {
    private final Map<String, List<OperatorMatch>> unaryProcessMap  = new HashMap<>();
    private final Map<String, List<OperatorMatch>> dyadicProcessMap = new HashMap<>();

    @Override
    public void registryOperator(String symbolName, Class<?> opeType, OperatorProcess process) {
        List<OperatorMatch> matchList = this.unaryProcessMap.computeIfAbsent(symbolName, k -> {
            return new ArrayList<>();
        });
        matchList.add(0, new UnaryProxyOperatorProcess(opeType, process));
    }

    @Override
    public void registryOperator(String symbolName, Class<?> fstType, Class<?> secType, OperatorProcess process) {
        List<OperatorMatch> matchList = this.dyadicProcessMap.computeIfAbsent(symbolName, k -> {
            return new ArrayList<>();
        });
        matchList.add(0, new DyadicProxyOperatorProcess(fstType, secType, process));
    }

    public OperatorProcess findUnaryProcess(String symbolName, Class<?> fstType) {
        if (fstType == null) {
            return null;
        }
        List<OperatorMatch> matchList = this.unaryProcessMap.get(symbolName);
        if (matchList == null || matchList.isEmpty()) {
            return null;
        }
        for (OperatorMatch item : matchList) {
            if (item.testMatch(fstType)) {
                return item;
            }
        }
        return null;
    }

    public OperatorProcess findDyadicProcess(String symbolName, Class<?> fstType, Class<?> secType) {
        if (fstType == null || secType == null) {
            return null;
        }
        List<OperatorMatch> matchList = this.dyadicProcessMap.get(symbolName);
        if (matchList == null || matchList.isEmpty()) {
            return null;
        }
        for (OperatorMatch item : matchList) {
            if (item.testMatch(fstType, secType)) {
                return item;
            }
        }
        return null;
    }

    private static Supplier<OperatorManager> operatorManager = new SingleProvider<>(OperatorManager::initManager);

    public static OperatorManager defaultManager() {
        return operatorManager.get();
    }

    private static OperatorManager initManager() {
        Class[] boolSet = { Boolean.TYPE, Boolean.class };
        Class[] classSet = { Boolean.TYPE, Boolean.class, Number.class };
        OperatorManager om = new OperatorManager();
        //
        // .一元运算(注册一元操作符，第二个操作数类型无效但是必须要有，所以给 Object)
        om.registryOperator("!", boolSet, new BooleanUOP());
        om.registryOperator("-", Number.class, new NumberUOP());
        // .通用类型运算
        om.registryOperator("!=", Object.class, Object.class, new ObjectEqDOP());
        om.registryOperator("==", Object.class, Object.class, new ObjectEqDOP());
        om.registryOperator("+", Object.class, Object.class, new StringJointDOP());
        // .二元，位运算
        om.registryOperator(">>>", classSet, classSet, new BinaryDOP());
        om.registryOperator(">>", classSet, classSet, new BinaryDOP());
        om.registryOperator("<<", classSet, classSet, new BinaryDOP());
        om.registryOperator("^", classSet, classSet, new BinaryDOP());
        om.registryOperator("|", classSet, classSet, new BinaryDOP());
        om.registryOperator("&", classSet, classSet, new BinaryDOP());
        // .二元，逻辑比较
        om.registryOperator("&&", boolSet, boolSet, new CompareDOP());
        om.registryOperator("||", boolSet, boolSet, new CompareDOP());
        // .二元，数值比较运算
        om.registryOperator("!=", classSet, classSet, new CompareDOP());
        om.registryOperator("==", classSet, classSet, new CompareDOP());
        om.registryOperator("<=", Number.class, Number.class, new CompareDOP());
        om.registryOperator("<", Number.class, Number.class, new CompareDOP());
        om.registryOperator(">=", Number.class, Number.class, new CompareDOP());
        om.registryOperator(">", Number.class, Number.class, new CompareDOP());
        // .二元，求值运算
        om.registryOperator("+", Number.class, Number.class, new NumberDOP());
        om.registryOperator("-", Number.class, Number.class, new NumberDOP());
        om.registryOperator("*", Number.class, Number.class, new NumberDOP());
        om.registryOperator("/", Number.class, Number.class, new NumberDOP());
        om.registryOperator("%", Number.class, Number.class, new NumberDOP());
        om.registryOperator("\\", Number.class, Number.class, new NumberDOP());
        //
        return om;
    }
}
